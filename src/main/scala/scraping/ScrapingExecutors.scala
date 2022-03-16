package scraping

import utils.Log.log

import java.time.LocalDate
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

case class ScrapingExecutors(dataSource: DataSource, startingDate: LocalDate, threads: Int) {
  private var executors = Seq[ExecutorService]()

  def startScraping(): Unit = {
    dataSource.zones.foreach(zone => {
      log("Retrieving houses in " + zone)
      val bagOfTasks = dataSource.createBagOfTasks(zone, startingDate)
      log("Houses found in " + zone + ": " + bagOfTasks.size)
      val executor: ExecutorService = Executors.newFixedThreadPool(threads)
      executors ++= Seq(executor)
      new Thread(() => {
        executor.invokeAll(bagOfTasks)
        executor.shutdown()
      }).start()
    })
    stopScraping()
  }

  private def stopScraping(): Unit = {
    println("Waiting for scraping to finish...")
    while(executors.nonEmpty){
      val e = executors.head
      executors = executors.drop(1)
      e.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)
    }
  }
}
