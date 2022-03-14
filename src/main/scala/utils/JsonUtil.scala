package utils

import com.google.gson.{GsonBuilder, JsonElement, JsonObject}

object JsonUtil {

  def getFromObjectHierarchy(jsonObject: JsonObject, hierarchy: Seq[String]): Option[JsonElement] = {
    var currentObject = jsonObject
    for (i <- 0 until hierarchy.size - 1) if (hasNotNullField(currentObject, hierarchy(i))) {
      currentObject = currentObject.getAsJsonObject(hierarchy(i))
    } else {
      return Option.empty
    }
    if(hasNotNullField(currentObject, hierarchy.last)){
      Option(currentObject.get(hierarchy.last))
    } else {
      Option.empty
    }
  }

  def addIfPresent(source: JsonObject, destination: JsonObject, addFieldName: String, sourceHierarchy: Seq[String]): Unit = {
    val toPut = getFromObjectHierarchy(source, sourceHierarchy)
    if(toPut.isDefined){
      destination.add(addFieldName, toPut.get)
    }
  }

  def hasNotNullField(jsonObj: JsonObject, field: String): Boolean =
    jsonObj.has(field) && !jsonObj.get(field).isJsonNull

  def getPrettyJson(json: JsonObject): String = new GsonBuilder().setPrettyPrinting().create().toJson(json)
}
