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
      print(".")
      var idSeq = extractIdSeq(createHouseListUrl(city, i))

      //get only one house for testing
      //canContinue = false
      //bagOfTasks.add(ImmobiliareItTask(idSeq.last))

      //page with too old houses (or no more results) reached
      if(idSeq.isEmpty || extractHouseDate(getHtmlString(idSeq.last)).compareTo(startingFrom) < 0){
        canContinue = false
        if(idSeq.nonEmpty){
          idSeq = idSeq.dropRight(1) //removing already analyzed house (if present)
        }
        while(idSeq.nonEmpty && extractHouseDate(getHtmlString(idSeq.last)).compareTo(startingFrom) < 0){
          idSeq = idSeq.dropRight(1) //removing too old houses
        }
      }
      idSeq.foreach(id => bagOfTasks.add(ImmobiliareItTask(id))) //filling bag of tasks with valid houses
      i += 1
    }
    print("\n")
    bagOfTasks
  }

}

private case class ImmobiliareItTask(id: Long) extends Task {
  override def call(): Unit = {
    val htmlString = getHtmlString(id)
    val date = extractHouseDate(htmlString)
    val json = getHouseJson(htmlString)
    val cleanJson = createCleanJson(date, json)
    val pw = new PrintWriter(new File("./scraped/" + id + ".json"))
    pw.write(getPrettyJson(cleanJson))
    pw.close()
  }

  private def getFromObjectHierarchy(jsonObject: JsonObject, hierarchy: String*): Option[JsonElement] = {
    var i = 0
    var currentObject = jsonObject
    while(i < hierarchy.size - 1) {
      if(hasNotNullField(currentObject, hierarchy(i))){
        currentObject = currentObject.getAsJsonObject(hierarchy(i))
        i += 1
      } else {
        return Option.empty
      }
    }

    if(hasNotNullField(currentObject, hierarchy.last)){
      Option(currentObject.get(hierarchy(i)))
    } else {
      Option.empty
    }
  }

  private def hasNotNullField(jsonObj: JsonObject, field: String): Boolean =
    jsonObj.has(field) && !jsonObj.get(field).isJsonNull

  private def createCleanJson(date: LocalDate, json: JsonObject): JsonObject = {
    val cleanJson = new JsonObject()
    val listing = json.getAsJsonObject("listing")
    cleanJson.addProperty("date", date.toString)
    cleanJson.add("id", listing.get("id"))
    cleanJson.add("contract", listing.get("contract"))
    cleanJson.add("title", listing.get("title"))
    val properties = listing.getAsJsonArray("properties").get(0).getAsJsonObject
    val condition = getFromObjectHierarchy(properties, "condition", "name")
    if (condition.isDefined){
      cleanJson.add("condition", condition.get)
    }
    val surface = "[0-9]*".r findFirstIn properties.get("surfaceValue").getAsString
    cleanJson.addProperty("surfaceValue", surface.getOrElse(""))
    cleanJson.add("typology", properties.getAsJsonObject("typology").get("name"))
    cleanJson.add("category", properties.getAsJsonObject("category").get("name"))
    val location = properties.getAsJsonObject("location")
    cleanJson.add("location", createClearLocationJson(location))
    cleanJson.add("price", properties.getAsJsonObject("price").get("price"))
    cleanJson.add("energy", properties.getAsJsonObject("energy").get("class"))
    cleanJson
  }

  private def createClearLocationJson(location: JsonObject): JsonObject = {
    val clearLocation = new JsonObject()
    clearLocation.add("latitude", location.get("latitude"))
    clearLocation.add("longitude", location.get("longitude"))
    clearLocation.add("nation", location.getAsJsonObject("nation").get("name"))
    clearLocation.add("region", location.getAsJsonObject("region").get("name"))
    clearLocation.add("province", location.getAsJsonObject("province").get("name"))
    clearLocation.add("city", location.getAsJsonObject("city").get("name"))
    clearLocation.add("macrozone", location.getAsJsonObject("macrozone").get("name"))
    clearLocation.add("locality", location.get("locality"))
    clearLocation.add("address", location.get("address"))
    clearLocation.add("streetNumber", location.get("streetNumber"))
    clearLocation
  }
}
