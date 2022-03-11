import immobiliareIt.ImmobiliareIt

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

object Main {

    def main(args: Array[String]): Unit = {
      val dataSource = ImmobiliareIt()
      val bagOfTasks = dataSource
        .createBagOfTasks("cesena", LocalDate.parse("10/03/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
      println(bagOfTasks.size)
      val exec: ExecutorService = Executors.newFixedThreadPool(8)
      exec.invokeAll(bagOfTasks)
      exec.shutdown()
      if(exec.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)) {
        println("Jobs completed")
      }
    }
}
