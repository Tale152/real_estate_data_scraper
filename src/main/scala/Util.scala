import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Util {

  private val day = "(([0-2][0-9])|(3[0-1]))"
  private val month = "((0[0-9])|(1[0-2]))"
  private val year = "((19[0-9][0-9])|(20[0-9][0-9]))"
  private val date = day + "/" + month + "/" + year
  private val dateFirstRegex = ("riferimento e Data annuncio</dt>(?s)(.*)" + date).r
  private val dateSecondRegex = date.r
  private val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  def extractHouseDate(html: String): Option[LocalDate] = {
    val firstFilter = dateFirstRegex findFirstIn html
    if(firstFilter.isDefined){
      val secondFilter = dateSecondRegex findFirstIn firstFilter.get
      if(secondFilter.isDefined){
        return Some(LocalDate.parse(secondFilter.get, format))
      }
    }
    None
  }

}
