package immobiliareIt

import java.time.LocalDate
import java.util
import ImmobiliareItUtil._
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
        val last = idSeq.last
        val lastHtml = getHtmlString(last)
        val lastDate = extractHouseDate(lastHtml)

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
    val json = getHouseJson(id)

    println(json.toString)
  }
}
