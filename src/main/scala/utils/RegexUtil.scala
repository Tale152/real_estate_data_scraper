package utils

/**
 * Contains the generic regexes that are not specific of any data source.
 */
object RegexUtil {
  val day = "(([0-2][0-9])|(3[0-1]))"
  val month = "((0[0-9])|(1[0-2]))"
  val year = "((19[0-9][0-9])|(20[0-9][0-9]))"
  val date: String = day + "/" + month + "/" + year
}
