import scalaj.http.Http
import java.util.concurrent.Callable

trait Task extends Callable[Unit]

case class CompleteScrapingTask(private val id: Long) extends Task{
  override def call(): Unit = {
    val htmlPage = Http("https://www.immobiliare.it/annunci/" + id + "/")
      .header("Content-Type", "text/html").header("Charset", "UTF-8")
      .asString
      .body

    println(Util.extractHouseDate(htmlPage).toString)
  }
}

case class PartialScrapingTask(private val html: String) extends Task{
  override def call(): Unit = {
    println(Util.extractHouseDate(html).toString)
  }
}
