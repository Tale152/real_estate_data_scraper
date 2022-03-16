package utils

/**
 * Log with a tag used by [[scraping.ScrapingExecutors]] to print information about the status of the scraping.
 */
object Log {

  def log(str: String): Unit = println("[SCRAPER] " + str)

}
