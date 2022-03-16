package utils

import java.io.{File, FileWriter}

/**
 * Utility methods used to handle [[File]].
 */
object FileUtil {

  private val path = "./scraped"
  private val fileExtension = ".json"
  private var fileWriter: Option[FileWriter] = Option.empty

  /**
   * If not present, create a directory to contain the results data in the current directory.
   * Create a [[FileWriter]] used to write in a file, which name is defined by the current millis.
   */
  def prepareResultDirectory(): Unit = {
    val resDirectory = new File(path)
    if(!resDirectory.exists()){
      resDirectory.mkdir()
      println("Created result directory " + path)
    }
    val millis = System.currentTimeMillis()
    fileWriter = Option(new FileWriter(path + "/" + millis + fileExtension))
    fileWriter.get.write("[") //to open a jsonArray containing all the results
  }

  /**
   * Append the content to the current content of the file.
   * @param content the string to append.
   */
  def appendFile(content: String): Unit = {
    fileWriter.get.append(content)
  }

  /**
   * Close the jsonArray containing all the JsonObject contents and close the file writer.
   */
  def closeFile(): Unit = {
    fileWriter.get.append("]") //to close a jsonArray containing all the results
    fileWriter.get.close()
  }
}
