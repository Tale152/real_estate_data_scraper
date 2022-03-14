package utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private object Flags{
  val SOURCE: String = "-s"
  val THREADS: String = "-t"
  val STARTING_DATE: String = "-d"
}

private object Regex{
  val SOURCE: String = Flags.SOURCE + "=(?s)(.*)"
  val THREADS: String = Flags.THREADS + "=[0-9]*"
  val STARTING_DATE: String = Flags.STARTING_DATE + "=" + RegexUtil.date
}

case class ArgsProvider(private val args: Array[String]) {

  def source: String = getArg(Regex.SOURCE, Flags.SOURCE, "Source (-s) needs to be specified").toLowerCase

  def startingDate: LocalDate = {
    val d = getArg(Regex.STARTING_DATE, Flags.STARTING_DATE, "Starting date (-d) needs to be specified (dd/mm/yyyy)")
    parseDate(d)
  }

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
