package immobiliareIt

import java.time.LocalDate
import java.util
import ImmobiliareItUtil._
import com.google.gson.JsonObject
import scraping.{DataSource, ResultsHandler, Task}
import utils.HtmlUtil.getHtmlString
import utils.JsonUtil
import utils.JsonUtil._

case class ImmobiliareIt() extends DataSource {

  private def createBagOfTasksFrom(city: String, startingFrom: LocalDate, sourceGenerator: (String, Int) => String): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    var i = 1
    var canContinue = true
    while (canContinue){
      print(".")
      var idSeq = extractIdSeq(sourceGenerator(city, i))
      if(idSeq.isEmpty){
        canContinue = false
      } else {
        var lastHtml = getHtmlString(createHouseUrl(idSeq.last))
        var lastDate = extractHouseDate(lastHtml)
        while(lastDate.isEmpty && idSeq.nonEmpty){
          idSeq = idSeq.dropRight(1)
          lastHtml = getHtmlString(createHouseUrl(idSeq.last))
          lastDate = extractHouseDate(lastHtml)
        }
        if(idSeq.isEmpty){
          canContinue = false
        } else {
          if(lastDate.get.compareTo(startingFrom) < 0){
            canContinue = false
            idSeq = idSeq.dropRight(1)
            while(idSeq.nonEmpty && extractHouseDate(getHtmlString(createHouseUrl(idSeq.last))).getOrElse(LocalDate.ofEpochDay(0)).compareTo(startingFrom) < 0){
              idSeq = idSeq.dropRight(1)
            }
          } else {
            bagOfTasks.add(HtmlAvailableTask(lastHtml))
            idSeq = idSeq.dropRight(1)
          }
        }
      }
      idSeq.foreach(id => bagOfTasks.add(CompleteTask(id))) //filling bag of tasks with valid houses
      i += 1
    }
    bagOfTasks
  }

  override def createBagOfTasks(city: String, startingFrom: LocalDate): util.HashSet[Task] = {
    val bagOfTasks = createBagOfTasksFrom(city, startingFrom, createSellingHouseListUrl)
    bagOfTasks.addAll(createBagOfTasksFrom(city, startingFrom, createRentingHouseListUrl))
    println("")
    bagOfTasks
  }

  override def zones: Seq[String] = Seq("agrigento-provincia", "alessandria-provincia"/*, "ancona-provincia",
  "aosta-provincia", "aquila-provincia", "arezzo-provincia", "ascoli-piceno-provincia",
  "asti-provincia", "avellino-provincia", "bari-provincia", "barletta-andria-trani-provincia",
  "belluno-provincia", "benevento-provincia", "bergamo-provincia", "biella-provincia",
  "bologna-provincia", "bolzano-provincia", "brescia-provincia", "brindisi-provincia", "cagliari-provincia",
  "caltanissetta-provincia", "campobasso-provincia", "caserta-provincia", "catania-provincia", "catanzaro-provincia",
  "chieti-provincia", "como-provincia", "cosenza-provincia", "cremona-provincia", "crotone-provincia",
  "cuneo-provincia", "enna-provincia", "fermo-provincia", "ferrara-provincia", "firenze-provincia",
  "foggia-provincia", "forli-cesena-provincia", "frosinone-provincia", "genova-provincia", "gorizia-provincia",
  "grosseto-provincia", "imperia-provincia", "isernia-provincia", "la-spezia-provincia", "latina-provincia",
  "lecce-provincia", "lecco-provincia", "livorno-provincia", "lodi-provincia", "lucca-provincia", "macerata-provincia",
  "mantova-provincia", "massa-carrara-provincia", "matera-provincia", "messina-provincia", "milano-provincia",
  "modena-provincia", "monza-brianza-provincia", "napoli-provincia", "novara-provincia", "nuoro-provincia",
  "oristano-provincia", "padova-provincia", "palermo-provincia", "parma-provincia", "pavia-provincia", "perugia-provincia",
  "pesaro-urbino-provincia", "pescara-provincia", "piacenza-provincia", "pisa-provincia", "pistoia-provincia",
  "pordenone-provincia", "potenza-provincia", "prato-provincia", "ragusa-provincia", "ravenna-provincia",
  "reggio-calabria-provincia", "reggio-emilia-provincia", "rieti-provincia", "rimini-provincia", "roma-provincia",
  "rovigo-provincia", "salerno-provincia", "san-marino-provincia", "sassari-provincia", "savona-provincia",
  "siena-provincia", "siracusa-provincia", "sondrio-provincia", "sud-sardegna-provincia", "taranto-provincia",
  "teramo-provincia", "terni-provincia", "torino-provincia", "trapani-provincia", "trento-provincia", "treviso-provincia",
  "trieste-provincia", "udine-provincia", "varese-provincia", "venezia-provincia", "verbania-provincia", "vercelli-provincia",
  "verona-provincia", "vibo-valentia-provincia", "vicenza-provincia", "viterbo-provincia"*/)
}

private case class CompleteTask(id: Long) extends Task {
  override def call(): Unit = HtmlAvailableTask(getHtmlString(createHouseUrl(id))).call()
}

private case class HtmlAvailableTask(html: String) extends Task {
  override def call(): Unit = {
    val date = extractHouseDate(html)
    if(date.isDefined){
      val json = getHouseJson(html)
      val cleanJson = createCleanJson(date.get, json)
      ResultsHandler.put(JsonUtil.getPrettyJson(cleanJson))
    }
  }

  private def createCleanJson(date: LocalDate, json: JsonObject): JsonObject = {
    val cleanJson = new JsonObject()
    cleanJson.addProperty("date", date.toString)
    if(hasNotNullField(json, "listing")){
      val listing = json.getAsJsonObject("listing")
      addIfPresent(listing, cleanJson, "id", Seq("id"))
      addIfPresent(listing, cleanJson, "contract", Seq("contract", "name"))
      addIfPresent(listing, cleanJson, "title", Seq("title"))
      if(hasNotNullField(listing, "properties")){
        val propertiesArray = listing.getAsJsonArray("properties")
        if(propertiesArray.size() > 0){
          val properties = propertiesArray.get(0).getAsJsonObject
          addIfPresent(properties, cleanJson, "condition", Seq("condition", "name"))
          addIfPresent(properties, cleanJson, "typology", Seq("typology", "name"))
          addIfPresent(properties, cleanJson, "category", Seq("category", "name"))
          addIfPresent(properties, cleanJson, "price", Seq("price", "price"))
          addIfPresent(properties, cleanJson, "energy", Seq("energy", "class"))
          cleanJson.add("location", createClearLocationJson(properties))

          val surface = "[0-9]*".r findFirstIn properties.get("surfaceValue").getAsString
          cleanJson.addProperty("surfaceValue", surface.getOrElse(""))
        }
      }
    }
    cleanJson
  }

  private def createClearLocationJson(properties: JsonObject): JsonObject = {
    val clearLocation = new JsonObject()
    if(hasNotNullField(properties, "location")){
      val location = properties.getAsJsonObject("location")
      addIfPresent(location, clearLocation, "latitude", Seq("latitude"))
      addIfPresent(location, clearLocation, "longitude", Seq("longitude"))
      addIfPresent(location, clearLocation, "nation", Seq("nation", "name"))
      addIfPresent(location, clearLocation, "region", Seq("region", "name"))
      addIfPresent(location, clearLocation, "province", Seq("province", "name"))
      addIfPresent(location, clearLocation, "city", Seq("city", "name"))
      addIfPresent(location, clearLocation, "macrozone", Seq("macrozone", "name"))
      addIfPresent(location, clearLocation, "locality", Seq("locality"))
      addIfPresent(location, clearLocation, "address", Seq("address"))
      addIfPresent(location, clearLocation, "streetNumber", Seq("streetNumber"))
    }
    clearLocation
  }
}
