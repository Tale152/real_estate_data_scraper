package utils

import utils.ArgsProviderUtil.{Flags, Regex}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Utility for [[ArgsProvider]], containing Flags and Regex only used in it.
 */
private object ArgsProviderUtil {
  /**
   * Flags that the [[ArgsProvider]] can accept.
   */
  object Flags {
    val SOURCE: String = "-s"
    val THREADS: String = "-t"
    val STARTING_DATE: String = "-d"
  }

  /**
   * Regex used in the [[ArgsProvider]] to check if the arguments are correct.
   */
  object Regex {
    val SOURCE: String = Flags.SOURCE + "=(?s)(.*)"
    val THREADS: String = Flags.THREADS + "=[0-9]*"
    val STARTING_DATE: String = Flags.STARTING_DATE + "=" + RegexUtil.date
  }
}

/**
 * Case class that retrieve and check the arguments in input.
 * @param args the arguments in input.
 */
case class ArgsProvider(private val args: Array[String]) {

  /**
   * @return the source argument.
   */
  def source: String = getArg(Regex.SOURCE, Flags.SOURCE, "Source (-s) needs to be specified").toLowerCase

  /**
   * @return the LocalDate argument.
   */
  def startingDate: LocalDate = {
    val d = getArg(Regex.STARTING_DATE, Flags.STARTING_DATE, "Starting date (-d) needs to be specified (dd/mm/yyyy)")
    parseDate(d)
  }

  /**
   * @return the number of threads argument.g
   */
  def threads: Int = {
    val t = getArg(Regex.THREADS, Flags.THREADS, "Threads number (-t) to be specified (integer)").toLowerCase
    if(!t.forall(c => c.isDigit)) throw new IllegalArgumentException("The provided argument -t is not an integer number")
    t.toInt
  }

  private def getArg(regex: String, flag: String, throwMsg: String): String = {
    if(args.count(s => s.matches(regex)) != 1) throw new IllegalArgumentException(throwMsg)
    args.find(s => s.matches(regex)).get.replace(flag + "=", "")
  }

  private def parseDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

}
