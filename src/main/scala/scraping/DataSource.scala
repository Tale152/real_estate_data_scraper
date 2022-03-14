package scraping

import java.time.LocalDate
import java.util

trait DataSource {
  def zones: Seq[String]
  def createBagOfTasks(city: String, startingFrom: LocalDate): util.HashSet[Task]
}
