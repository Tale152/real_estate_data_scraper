import BagOfTasks.createBagOfTasks

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

object Main {

    def main(args: Array[String]): Unit = {
      val bagOfTasks = createBagOfTasks("cesena")
      println(bagOfTasks.size)
      val exec: ExecutorService = Executors.newFixedThreadPool(4)
      exec.invokeAll(bagOfTasks)
      exec.shutdown()
      if(exec.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)) {
        println("Jobs completed")
      }
    }
}
