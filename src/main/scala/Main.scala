import scalaj.http.Http

object Main {

    def main(args: Array[String]): Unit = {
      val result = Http("https://www.immobiliare.it/vendita-case/cesena/?criterio=dataModifica&ordine=desc")
        .header("Content-Type", "text/html")
        .header("Charset", "UTF-8")
        .asString
        .body
      println(result)
    }
}
