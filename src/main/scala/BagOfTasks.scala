import java.time.LocalDate
import java.util

object BagOfTasks{

  def createBagOfTasks(city: String, startingFrom: LocalDate): util.HashSet[Task] = {
    val bagOfTasks = new util.HashSet[Task]()
    var i = 1
    var canContinue = true
    while (canContinue){
      println("page " + i)
      var idSeq = Util.extractIdSeq(createHouseListUrl(city, i))
      if(idSeq.isEmpty){
        canContinue = false
      } else {
        val last = idSeq.last
        val lastHtml = Util.getHtmlString(last)
        val lastDate = Util.extractHouseDate(lastHtml)
        idSeq = idSeq.dropRight(1)
        if(lastDate.compareTo(startingFrom) < 0){
          canContinue = false
          //TODO retrieve eventual houses to be added
        } else {
          idSeq.foreach(id => bagOfTasks.add(CompleteScrapingTask(id)))
          bagOfTasks.add(PartialScrapingTask(lastHtml))
        }
        i += 1
      }
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
