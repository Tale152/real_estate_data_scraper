package utils

import com.google.gson.JsonObject
import utils.JsonUtil.getPrettyJson
import utils.Log.log

import java.io.{File, PrintWriter}

object FileUtil {

  private val path = "./scraped"

  def prepareResultDirectory(): Unit = {
    val resDirectory = new File(path)
    if(!resDirectory.exists()){
      resDirectory.mkdir()
      log("Created result directory " + path)
    }
    resDirectory.listFiles.foreach(f => f.delete)
    log("Cleaned result directory " + path)
  }

  def writeFile(id: Long, json: JsonObject) : Unit = {
    val pw = new PrintWriter(new File(path + "/" + id + ".json"))
    pw.write(getPrettyJson(json))
    pw.close()
  }
}
