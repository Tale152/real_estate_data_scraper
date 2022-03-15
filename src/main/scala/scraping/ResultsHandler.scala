package scraping

import utils.FileUtil

import java.util.concurrent.locks.{Lock, ReentrantLock}

object ResultsHandler {
  private val fullBuffer = 400
  private val mutex: Lock = new ReentrantLock()
  private var buffer: Seq[String] = Seq()
  private var firstWrite = true

  def put(result: String): Unit = handleMutex(_ => handlePut(result))

  def complete(): Unit = handleMutex(_ => handleComplete())

  private def handleMutex(strategy: Unit => Unit): Unit ={
    try {
      mutex.lock()
      strategy()
    } finally {
      mutex.unlock()
    }
  }

  private def handlePut(result: String): Unit ={
    buffer = buffer :+ result
    if (buffer.size >= fullBuffer) {
      saveBuffer()
    }
  }

  private def handleComplete(): Unit = {
    if (buffer.nonEmpty){
      saveBuffer()
    }
  }

  private def saveBuffer(): Unit ={
    var first = buffer.head
    if (!firstWrite) {
      first = ",\n" + first
    } else {
      firstWrite = false
    }
    buffer = buffer.drop(1)
    val result = buffer.foldLeft(first)((s1,s2) => s1 + ",\n" + s2)
    FileUtil.appendFile(result)
    buffer = Seq()
  }

}
