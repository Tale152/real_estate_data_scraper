package utils

import scalaj.http.Http

object HtmlUtil {

  def getHtmlString(url: String): String = Http(url)
    .header("Content-Type", "text/html")
    .header("Charset", "UTF-8")
    .asString
    .body
}
