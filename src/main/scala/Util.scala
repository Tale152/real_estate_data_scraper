import scalaj.http.Http

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex

object Util {

  private val idHrefPattern = "href=\"https://www.immobiliare.it/annunci/[0-9]*/\"".r
  private val day = "(([0-2][0-9])|(3[0-1]))"
  private val month = "((0[0-9])|(1[0-2]))"
  private val year = "((19[0-9][0-9])|(20[0-9][0-9]))"
  private val date = day + "/" + month + "/" + year
  private val dateFirstRegex = ("riferimento e Data annuncio</dt>(?s)(.*)" + date).r
  private val dateSecondRegex = date.r
  private val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  def getHtmlString(id: Long): String = Http("https://www.immobiliare.it/annunci/" + id + "/")
      .header("Content-Type", "text/html").header("Charset", "UTF-8")
      .asString
      .body

  def extractIdSeq(url: String): Seq[Long] = {
    val htmlPage = Http(url).header("Content-Type", "text/html").header("Charset", "UTF-8").asString.body
    (idHrefPattern findAllMatchIn htmlPage).toSeq.map(idHrefMatch => extractId(idHrefMatch))
  }

  private def extractId(href: Regex.Match): Long = href.toString()
    .replace("href=\"https://www.immobiliare.it/annunci/", "")
    .replace("/\"", "")
    .toLong

  def extractHouseDate(html: String): LocalDate = {
    val firstFilter = dateFirstRegex findFirstIn html
    if(firstFilter.isDefined){
      val secondFilter = dateSecondRegex findFirstIn firstFilter.get
      if(secondFilter.isDefined){
        return LocalDate.parse(secondFilter.get, format)
      }
    }
    throw new IllegalStateException("Cannot extract house date")
  }

}
