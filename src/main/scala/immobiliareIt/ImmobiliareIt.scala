package immobiliareIt

import java.time.LocalDate
import java.util
import ImmobiliareItUtil._
import com.google.gson.{JsonElement, JsonObject}
import scraping.{DataSource, Task}

import java.io.{File, PrintWriter}

case class ImmobiliareIt() extends DataSource {

  override def createBagOfTasks(city: String, startingFrom: LocalDate): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    var i = 1
    var canContinue = true
    while (canContinue){
      var idSeq = extractIdSeq(createHouseListUrl(city, i))
      if(idSeq.isEmpty){
        canContinue = false
      } else {
        val lastHtml = getHtmlString(idSeq.last)
        if(extractHouseDate(lastHtml).compareTo(startingFrom) < 0){
          canContinue = false
          idSeq = idSeq.dropRight(1)
          while(idSeq.nonEmpty && extractHouseDate(getHtmlString(idSeq.last)).compareTo(startingFrom) < 0){
            idSeq = idSeq.dropRight(1)
          }
        } else {
          bagOfTasks.add(HtmlAvailableTask(lastHtml, idSeq.last))
          idSeq = idSeq.dropRight(1)
        }
      }
      idSeq.foreach(id => bagOfTasks.add(CompleteTask(id))) //filling bag of tasks with valid houses
      i += 1
    }
    bagOfTasks
  }

}

private case class CompleteTask(id: Long) extends Task {
  override def call(): Unit = HtmlAvailableTask(getHtmlString(id), id).call()
}

private case class HtmlAvailableTask(html: String, id: Long) extends Task {
  override def call(): Unit = {
    val date = extractHouseDate(html)
    val json = getHouseJson(html)
    val cleanJson = createCleanJson(date, json)
    val pw = new PrintWriter(new File("./scraped/" + id + ".json"))
    pw.write(getPrettyJson(cleanJson))
    pw.close()
  }

  private def getFromObjectHierarchy(jsonObject: JsonObject, hierarchy: Seq[String]): Option[JsonElement] = {
    var currentObject = jsonObject
    for (i <- 0 until hierarchy.size - 1) if (hasNotNullField(currentObject, hierarchy(i))) {
      currentObject = currentObject.getAsJsonObject(hierarchy(i))
    } else {
      return Option.empty
    }
    if(hasNotNullField(currentObject, hierarchy.last)){
      Option(currentObject.get(hierarchy.last))
    } else {
      Option.empty
    }
  }

  private def addIfPresent(source: JsonObject, destination: JsonObject, addFieldName: String, sourceHierarchy: Seq[String]): Unit = {
    val toPut = getFromObjectHierarchy(source, sourceHierarchy)
    if(toPut.isDefined){
      destination.add(addFieldName, toPut.get)
    }
  }

  private def hasNotNullField(jsonObj: JsonObject, field: String): Boolean =
    jsonObj.has(field) && !jsonObj.get(field).isJsonNull

  private def createCleanJson(date: LocalDate, json: JsonObject): JsonObject = {
    val cleanJson = new JsonObject()
    cleanJson.addProperty("date", date.toString)
    if(hasNotNullField(json, "listing")){
      val listing = json.getAsJsonObject("listing")
      addIfPresent(listing, cleanJson, "id", Seq("id"))
      addIfPresent(listing, cleanJson, "contract", Seq("contract"))
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
