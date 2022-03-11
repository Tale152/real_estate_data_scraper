import java.time.LocalDate
import java.util

trait DataSource {
  def createBagOfTasks(city: String, startingFrom: LocalDate): util.HashSet[Task]
}

