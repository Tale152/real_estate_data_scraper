import org.htmlcleaner.HtmlCleaner

import java.net.URL
import java.util.concurrent.Callable

trait Task extends Callable[Unit]

case class CompleteScrapingTask(private val id: Long) extends Task{
  override def call(): Unit = {
    val body = new HtmlCleaner()
      .clean(new URL("https://www.immobiliare.it/annunci/" + id + "/"))
      .findElementByName("body", false)

    println(body
      .findElementByAttValue("class", "im-titleBlock__title", true, true)
      .getText
      .toString
    )
  }
}
