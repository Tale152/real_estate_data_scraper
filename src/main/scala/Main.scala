import immobiliareIt.ImmobiliareIt
import scraping.DataSource
import utils.{ArgsProvider, FileUtil, Sources}
import utils.Log.log

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

object Main {

    def main(args: Array[String]): Unit = {
      val argsProvider = ArgsProvider(args)
      val executor: ExecutorService = Executors.newFixedThreadPool(argsProvider.threads)
      val dataSource: DataSource = getDataSource(argsProvider.source)
      FileUtil.prepareResultDirectory()

      log("Retrieving houses from " + argsProvider.source)
      val bagOfTasks = dataSource.createBagOfTasks("cesena", argsProvider.startingDate)
      log("Houses found: " + bagOfTasks.size)
      log("Scraping...")
      executor.invokeAll(bagOfTasks)
      executor.shutdown()
      if(executor.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)) {
        log("Tasks completed")
      }
    }

    private def getDataSource(s: String): DataSource = s match {
      case Sources.IMMOBILIARE_IT => ImmobiliareIt()
      case _ => throw new IllegalArgumentException("The specified source (-s) does not correspond to any known source")
    }

}
