package utils

import scalaj.http.Http

/**
 * Utility methods to handle Http calls.
 */
object HtmlUtil {

  /**
   * Get the html body found by calling a specific url.
   * @param url a string that is the url of the html needed.
   * @return a string which is the body of the html found by the Http call to the specified url.
   */
  def getHtmlString(url: String): String = Http(url)
    .header("Content-Type", "text/html")
    .header("Charset", "UTF-8")
    .asString
    .body
}
