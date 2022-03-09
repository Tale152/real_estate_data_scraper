import org.htmlcleaner.{CleanerProperties, HtmlCleaner}
import scalaj.http.Http

import java.net.URL

object Main {

    def main(args: Array[String]): Unit = {
      /*val result = Http("https://www.immobiliare.it/vendita-case/cesena/?criterio=dataModifica&ordine=desc")
        .header("Content-Type", "text/html")
        .header("Charset", "UTF-8")
        .asString
        .body*/
      //println(result)

      val prop = new CleanerProperties()
      for (i <- 1 to 3){
        //getting html from the url
        val houseList = new HtmlCleaner(prop)
          .clean(new URL("https://www.immobiliare.it/vendita-case/cesena/?criterio=dataModifica&ordine=desc&pag=" + i))
          .findElementByName("body", false)
          .findElementByAttValue("class", "nd-list in-realEstateResults", true, true)
        if(houseList != null){
          val houseBodies = houseList
            .getElementsByName("a", true)
            .map(e => new HtmlCleaner(prop)
              .clean(new URL(e.getAttributeByName("href")))
              .findElementByName("body", false)
            )
          houseBodies.foreach(h =>
            println(h.findElementByAttValue("class", "im-titleBlock__title", true, true).getText.toString)
          )
        }
      }
    }
}
