package immobiliareIt

import scalaj.http.Http

import java.time.LocalDate
import java.util
import _root_.DataSource
import _root_.Task
import ImmobiliareItUtil._

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
          idSeq.foreach(id => bagOfTasks.add(ImmobiliareItTask(id)))
        }
        i += 1
      }
    }
    bagOfTasks
  }

}

private case class ImmobiliareItTask(id: Long) extends Task {
  override def call(): Unit = {
    val htmlPage = Http("https://www.immobiliare.it/annunci/" + id + "/")
      .header("Content-Type", "text/html").header("Charset", "UTF-8")
      .asString
      .body

    println(extractHouseDate(htmlPage).toString)
  }
}
