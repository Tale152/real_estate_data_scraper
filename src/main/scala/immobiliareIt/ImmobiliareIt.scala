package immobiliareIt

import java.time.LocalDate
import java.util
import ImmobiliareItUtil._
import com.google.gson.{GsonBuilder, JsonObject}
import scraping.{DataSource, Task}

case class ImmobiliareIt() extends DataSource {

  override def createBagOfTasks(city: String, startingFrom: LocalDate): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    var i = 1
    var canContinue = true
    while (canContinue){
      println("page " + i)
      var idSeq = extractIdSeq(createHouseListUrl(city, i))
      if(idSeq.isEmpty){
        canContinue = false
      } else {
        val lastDate = extractHouseDate(getHtmlString(idSeq.last))
        if(lastDate.compareTo(startingFrom) < 0){
          canContinue = false
          idSeq = idSeq.dropRight(1)

          //TODO retrieve eventual houses to be added
        } else {
          bagOfTasks.add(ImmobiliareItTask(idSeq.last))
          canContinue = false
          //idSeq.foreach(id => bagOfTasks.add(ImmobiliareItTask(id)))
        }
        i += 1
      }
    }
    bagOfTasks
  }

}

private case class ImmobiliareItTask(id: Long) extends Task {
  override def call(): Unit = {
    val htmlString = getHtmlString(id)
    val date = extractHouseDate(htmlString)
    val json = getHouseJson(htmlString)
    val cleanJson = createCleanJson(date, json)
    println(
      new GsonBuilder()
      .setPrettyPrinting()
      .create()
      .toJson(cleanJson)
    )
  }

  private def createCleanJson(date: LocalDate, json: JsonObject): JsonObject = {
    val cleanJson = new JsonObject()
    val listing = json.getAsJsonObject("listing")
    cleanJson.addProperty("date", date.toString)
    cleanJson.add("id", listing.get("id"))
    cleanJson.add("contract", listing.get("contract"))
    cleanJson.add("title", listing.get("title"))
    val properties = listing.getAsJsonArray("properties").get(0).getAsJsonObject
    cleanJson.add("condition", properties.getAsJsonObject("condition").get("name"))
    cleanJson
  }
}
