import immobiliareIt.ImmobiliareIt
import scraping.{DataSource, ResultsHandler, ScrapingExecutors}
import utils.{ArgsProvider, FileUtil, Sources}

object Main {

    def main(args: Array[String]): Unit = {
      val argsProvider = ArgsProvider(args)
      val dataSource: DataSource = getDataSource(argsProvider.source)
      FileUtil.prepareResultDirectory()
      ScrapingExecutors(dataSource, argsProvider.startingDate, argsProvider.threads).startScraping()
      ResultsHandler.complete()
      FileUtil.closeFile()
      println("Scraping completed")
    }

    private def getDataSource(s: String): DataSource = s match {
      case Sources.IMMOBILIARE_IT => ImmobiliareIt()
      case _ => throw new IllegalArgumentException("The specified source (-s) does not correspond to any known source")
    }

}
