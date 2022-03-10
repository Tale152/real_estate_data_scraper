import scalaj.http.Http

import java.util

object BagOfTasks{

  def createBagOfTasks(city: String): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    for (i <- 1 to 2){
      val idSeq = Util.extractIdSeq(createHouseListUrl(city, i))
      idSeq.foreach(id => bagOfTasks.add(CompleteScrapingTask(id)))
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

}
