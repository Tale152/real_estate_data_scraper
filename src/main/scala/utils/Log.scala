package utils

/**
 * Log with a tag to print information about the status of the scraping.
 */
object Log {

  /**
   * Print a string with the tag [SCRAPER], used to recognize the prints made by [[scraping.ScrapingExecutors]].
   * @param str the information that the executor specify.
   */
  def log(str: String): Unit = println("[SCRAPER] " + str)

}
