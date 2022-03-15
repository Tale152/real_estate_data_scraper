import immobiliareIt.ImmobiliareIt
import scraping.{DataSource, ResultsHandler}
import utils.{ArgsProvider, FileUtil, Sources}
import utils.Log.log

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

object Main {

    def main(args: Array[String]): Unit = {
      val argsProvider = ArgsProvider(args)
      val dataSource: DataSource = getDataSource(argsProvider.source)
      FileUtil.prepareResultDirectory()

      var executors = Seq[ExecutorService]()
      dataSource.zones.foreach(zone => {
        log("Retrieving houses in " + zone)
        val bagOfTasks = dataSource.createBagOfTasks(zone, argsProvider.startingDate)
        log("Houses found in " + zone + ": " + bagOfTasks.size)
        val executor: ExecutorService = Executors.newFixedThreadPool(argsProvider.threads)
        executors ++= Seq(executor)
        new Thread(() => {
          executor.invokeAll(bagOfTasks)
          executor.shutdown()
        }).start()
      })
      println("Waiting for scraping to finish...")
      while(executors.nonEmpty){
        val e = executors.head
        executors = executors.drop(1)
        e.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)
      }
      ResultsHandler.complete()
      FileUtil.closeFile()
      log("Scraping completed")
    }

    private def getDataSource(s: String): DataSource = s match {
      case Sources.IMMOBILIARE_IT => ImmobiliareIt()
      case _ => throw new IllegalArgumentException("The specified source (-s) does not correspond to any known source")
    }

}
