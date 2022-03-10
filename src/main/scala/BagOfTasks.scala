import org.htmlcleaner.HtmlCleaner

import java.net.URL
import java.util

object BagOfTasks{

  def createBagOfTasks(city: String): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    for (i <- 1 to 2){
      val houseList = new HtmlCleaner()
        .clean(createHouseListUrl(city, i))
        .findElementByName("body", false)
        .findElementByAttValue("class", "nd-list in-realEstateResults", true, true)
      if(houseList != null){
        houseList
          .getElementsByName("a", true)
          .map(e => extractIdFromHouseUrl(e.getAttributeByName("href")))
          .foreach(id => bagOfTasks.add(CompleteScrapingTask(id)))
      }
    }
    bagOfTasks
  }

  private def createHouseListUrl(city: String, i: Int): URL =
    new URL("https://www.immobiliare.it/vendita-case/" + city + "/?criterio=dataModifica&ordine=desc&pag=" + i)

  private def extractIdFromHouseUrl(url: String): Long = url
    .replace("https://www.immobiliare.it/annunci/", "")
    .replace("/", "")
    .toLong

}
