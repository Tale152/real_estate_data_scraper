import org.htmlcleaner.HtmlCleaner

import java.net.URL
import java.util

object BagOfTasks{

  def createBagOfTasks(city: String): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    for (i <- 1 to 2){
      val houseList = new HtmlCleaner()
        .clean(new URL("https://www.immobiliare.it/vendita-case/" + city + "/?criterio=dataModifica&ordine=desc&pag=" + i))
        .findElementByName("body", false)
        .findElementByAttValue("class", "nd-list in-realEstateResults", true, true)
      if(houseList != null){
        houseList
          .getElementsByName("a", true)
          .map(e => e.getAttributeByName("href")
            .replace("https://www.immobiliare.it/annunci/", "")
            .replace("/", "")
            .toLong
          )
          .foreach(id => bagOfTasks.add(CompleteScrapingTask(id)))
      }
    }
    bagOfTasks
  }
}
