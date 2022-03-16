package utils

import com.google.gson.{GsonBuilder, JsonElement, JsonObject}

/**
 * Utility methods to handle [[JsonObject]].
 */
object JsonUtil {

  /**
   * Starting from a [[JsonObject]] it is possible to search for a specific property, following the hierarchy
   * specified by the sequence of keys.
   * @param jsonObject the JsonObject that is going to be navigated.
   * @param hierarchy a Seq of String in which every String is a key in the JsonObject. The keys are going to be
   *                  searched in the JsonObject sequentially, so they must follow the JsonObject structure.
   * @return Option.Empty if the JsonElement searched does not exist, or an Option[JsonElement] if the
   *         the navigation of the JsonObject has been done successfully.
   */
  def getFromObjectHierarchy(jsonObject: JsonObject, hierarchy: Seq[String]): Option[JsonElement] = {
    var currentObject = jsonObject
    //searching jsonObject until it is not the last string in the hierarchy
    for (i <- 0 until hierarchy.size - 1) if (hasNotNullField(currentObject, hierarchy(i))) {
      currentObject = currentObject.getAsJsonObject(hierarchy(i))
    } else {
      return Option.empty
    }
    //extract the last string in the hierarchy, which is not a jsonObject but a property
    if(hasNotNullField(currentObject, hierarchy.last)){
      Option(currentObject.get(hierarchy.last))
    } else {
      Option.empty
    }
  }

  /**
   * Search for a specific property in a [[JsonObject]] navigating that json following the hierarchy specified.
   * If that property exists, then it is added to the destination jsonObject, with a specified field name.
   * @param source the JsonObject in which is going to be searched a specific property using the hierarchy.
   * @param destination the JsonObject to update in case the property in the source has been found.
   * @param addFieldName the field name to add to the destination JsonObject if the property in the source has been
   *                     found.
   * @param sourceHierarchy a Seq of String in which every String is a key in the source JsonObject.
   *                        The keys are going to be searched in the JsonObject sequentially, so they must follow
   *                        the JsonObject structure.
   */
  def addIfPresent(source: JsonObject, destination: JsonObject, addFieldName: String, sourceHierarchy: Seq[String]): Unit = {
    val toPut = getFromObjectHierarchy(source, sourceHierarchy)
    if(toPut.isDefined){
      //if the property has been found, add it to the destination with the specified field name
      destination.add(addFieldName, toPut.get)
    }
  }

  /**
   * Check if a specified field of a [[JsonObject]] is present and it does not contain a null JsonObject.
   * @param jsonObj the JsonObject which has to be checked.
   * @param field the field that is going to be search in the JsonObject to check if it is present and it is not
   *              null.
   * @return true if the field is present in the JsonObject and if that field is not null, false otherwise.
   */
  def hasNotNullField(jsonObj: JsonObject, field: String): Boolean =
    jsonObj.has(field) && !jsonObj.get(field).isJsonNull

  /**
   * Json print formatter, used to format in a pretty way the [[JsonObject]], ready to be printed.
   * @param json the JsonObject that is going to be formatted.
   * @return a String which contains the JsonObject formatted in a readable way.
   */
  def getPrettyJson(json: JsonObject): String = new GsonBuilder().setPrettyPrinting().create().toJson(json)
}
