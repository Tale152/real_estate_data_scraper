package immobiliareIt

import com.google.gson.{Gson, JsonObject, JsonParser}
import scalaj.http.Http

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex

object ImmobiliareItUtil {

  private val idHrefPattern = "href=\"https://www.immobiliare.it/annunci/[0-9]*/\"".r
  private val day = "(([0-2][0-9])|(3[0-1]))"
  private val month = "((0[0-9])|(1[0-2]))"
  private val year = "((19[0-9][0-9])|(20[0-9][0-9]))"
  private val date = day + "/" + month + "/" + year
  private val dateFirstRegex = ("riferimento e Data annuncio</dt>(?s)(.*)" + date).r
  private val dateSecondRegex = date.r
  private val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  private val bodyRegex = "<body(?s)(.*)</body>".r

  def getHtmlString(id: Long): String = {
    val htmlPage = Http("https://www.immobiliare.it/annunci/" + id + "/")
      .header("Content-Type", "text/html").header("Charset", "UTF-8")
      .asString
      .body
    val bodyRegexMatch = bodyRegex findFirstIn htmlPage
    val body = bodyRegexMatch.getOrElse(throw new IllegalStateException("No body regex matched"))
    "<script(?s)(.*)</script>".r.replaceAllIn(body,"")
  }

  def getHouseJson(id: Long): JsonObject = {
    val htmlPage = Http("https://www.immobiliare.it/annunci/" + id + "/")
      .header("Content-Type", "text/html").header("Charset", "UTF-8")
      .asString
      .body
    val openingTag = "<script type=\"application/json\" id=\"js-hydration\">"
    val closingTag = "</script>"
    val scriptRegex = (openingTag + "(?s)(.*)" + closingTag).r
    val script = scriptRegex findFirstIn htmlPage getOrElse(throw new IllegalStateException("No main content script regex matched"))
    JsonParser.parseString(script.replace(openingTag, "").replace(closingTag, "")).getAsJsonObject
  }

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
    if (firstFilter.isDefined) {
      val secondFilter = dateSecondRegex findFirstIn firstFilter.get
      if (secondFilter.isDefined) {
        return LocalDate.parse(secondFilter.get, format)
      }
    }
    throw new IllegalStateException("Cannot extract house date")
  }

  def extractHouseCoordinates(html: String): (Option[String], Option[String]) ={
    val lat = "\"lat\":[0-9]*.[0-9]*".r findFirstIn html
    val lng = "\"lng\":[0-9]*.[0-9]*".r findFirstIn html
    (lat, lng)
  }

  def createHouseListUrl(city: String, i: Int): String = {
    var res = "https://www.immobiliare.it/vendita-case/" + city + "/?criterio=dataModifica&ordine=desc"
    if(i > 1){
      res += "&pag=" + i
    }
    res
  }

}
