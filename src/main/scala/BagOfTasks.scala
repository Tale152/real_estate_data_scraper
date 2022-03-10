import scalaj.http.Http

import java.util

object BagOfTasks{

  def createBagOfTasks(city: String): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    for (i <- 1 to 2){
      val htmlPage = Http(createHouseListUrl(city, i)).header("Content-Type", "text/html").header("Charset", "UTF-8")
        .asString
        .body
      val pattern = "href=\"https://www.immobiliare.it/annunci/[0-9]*/\"".r
      val matched = pattern findAllMatchIn htmlPage
      matched.foreach(m => bagOfTasks.add(CompleteScrapingTask(extractIdFromHouseHref(m.toString()))))
    }
    bagOfTasks
  }

  private def createHouseListUrl(city: String, i: Int): String = {
    if(i == 1){
      "https://www.immobiliare.it/vendita-case/" + city + "/?criterio=dataModifica&ordine=desc"
    } else {
      "https://www.immobiliare.it/vendita-case/" + city + "/?criterio=dataModifica&ordine=desc&pag=" + i
    }
  }

  private def extractIdFromHouseHref(href: String): Long = href
    .replace("href=\"https://www.immobiliare.it/annunci/", "")
    .replace("/\"", "")
    .toLong

}
