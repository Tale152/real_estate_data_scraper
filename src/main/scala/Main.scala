import immobiliareIt.ImmobiliareIt

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

object Main {

    def main(args: Array[String]): Unit = {
      prepareResultDirectory()
      println("Generating tasks")
      val dataSource = ImmobiliareIt()
      val bagOfTasks = dataSource.createBagOfTasks("cesena", parseDate("10/03/2022"))
      println("Bag of tasks size: " + bagOfTasks.size)
      val exec: ExecutorService = Executors.newFixedThreadPool(16)
      exec.invokeAll(bagOfTasks)
      exec.shutdown()
      if(exec.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)) {
        println("Jobs completed")
      }
    }

    def prepareResultDirectory(): Unit = {
      val resDirectory = new File("./scraped")
      if(!resDirectory.exists()){
        resDirectory.mkdir()
      }
      resDirectory.listFiles.foreach(f => f.delete)
    }

    def parseDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}
