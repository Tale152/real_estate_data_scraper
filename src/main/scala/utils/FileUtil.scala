package utils

import com.google.gson.JsonObject
import utils.JsonUtil.getPrettyJson

import java.io.{File, PrintWriter}

object FileUtil {

  private val path = "./scraped"

  def prepareResultDirectory(): Unit = {
    val resDirectory = new File(path)
    if(!resDirectory.exists()){
      resDirectory.mkdir()
    }
    resDirectory.listFiles.foreach(f => f.delete)
  }

  def writeFile(id: Long, json: JsonObject) : Unit = {
    val pw = new PrintWriter(new File(path + id + ".json"))
    pw.write(getPrettyJson(json))
    pw.close()
  }
}
