package immobiliareIt

import com.google.gson.{JsonObject, JsonParser}
import utils.{HtmlUtil, RegexUtil}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex

protected object ImmobiliareItUtil {

  private val immobiliareItUrl = "https://www.immobiliare.it/"
  private val houseUrlEnding = "/?criterio=dataModifica&ordine=desc"
  private val idHrefPattern = "href=\"https://www.immobiliare.it/annunci/[0-9]*/\"".r
  private val dateFirstRegex = ("riferimento e Data annuncio</dt>(?s)(.*)" + RegexUtil.date).r
  private val dateSecondRegex = RegexUtil.date.r
  private val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  def getHouseJson(htmlPage: String): JsonObject = {
    val openingTag = "<script type=\"application/json\" id=\"js-hydration\">"
    val closingTag = "</script>"
    val scriptRegex = (openingTag + "(?s)(.*)" + closingTag).r
    val script = scriptRegex findFirstIn htmlPage getOrElse(throw new IllegalStateException("No main content script regex matched"))
    JsonParser.parseString(script.replace(openingTag, "").replace(closingTag, "")).getAsJsonObject
  }

  def extractIdSeq(url: String): Seq[Long] =
    (idHrefPattern findAllMatchIn HtmlUtil.getHtmlString(url)).toSeq.map(idHrefMatch => extractId(idHrefMatch))

  private def extractId(href: Regex.Match): Long = href.toString()
    .replace("href=\"" + immobiliareItUrl + "annunci/", "")
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

  def createRentingHouseListUrl(city: String, i: Int): String = createHouseListUrl(city, i, "affitto-case/")

  def createSellingHouseListUrl(city: String, i: Int): String = createHouseListUrl(city, i, "vendita-case/")

  private def createHouseListUrl(city: String, i: Int, contractType: String): String = {
    var res = immobiliareItUrl + contractType + city + houseUrlEnding
    if(i > 1){
      res += "&pag=" + i
    }
    res
  }

  def createHouseUrl(id: Long): String = immobiliareItUrl + "annunci/" + id + "/"

}
