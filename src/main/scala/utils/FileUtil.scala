package utils

import utils.Log.log

import java.io.{File, FileWriter}

object FileUtil {

  private val path = "./scraped"
  private var fileWriter: Option[FileWriter] = Option.empty

  def prepareResultDirectory(): Unit = {
    val resDirectory = new File(path)
    if(!resDirectory.exists()){
      resDirectory.mkdir()
      log("Created result directory " + path)
    }
    val millis = System.currentTimeMillis()
    fileWriter = Option(new FileWriter(path + "/" + millis + ".json"))
    fileWriter.get.write("[")
  }

  def appendFile(content: String): Unit = {
    fileWriter.get.append(content)
  }

  def closeFile(): Unit = {
    fileWriter.get.append("]")
    fileWriter.get.close()
  }
}
