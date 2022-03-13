package immobiliareIt

import java.time.LocalDate
import java.util
import ImmobiliareItUtil._
import com.google.gson.JsonObject
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
