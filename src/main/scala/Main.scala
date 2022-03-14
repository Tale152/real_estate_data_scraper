import immobiliareIt.ImmobiliareIt
import utils.FileUtil

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

object Main {

    def main(args: Array[String]): Unit = {
      FileUtil.prepareResultDirectory()
      println("Generating tasks")
      val dataSource = ImmobiliareIt()
      val bagOfTasks = dataSource.createBagOfTasks("cesena", parseDate("11/03/2022"))
      println("Bag of tasks size: " + bagOfTasks.size)
      val exec: ExecutorService = Executors.newFixedThreadPool(16)
      exec.invokeAll(bagOfTasks)
      exec.shutdown()
      if(exec.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)) {
        println("Jobs completed")
      }
    }

    def parseDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}
