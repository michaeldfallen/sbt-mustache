package io.michaelallen.logging

import org.slf4j.LoggerFactory

trait Timing {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def time[A](message: String)(block: => A):A = {
    val timeAtStartMs = System.currentTimeMillis() % 1000;
    val result: A = block
    val timeAtEndMs = System.currentTimeMillis() % 1000;
    val elapsed = timeAtEndMs - timeAtStartMs
    logger.debug(s"$message took $elapsed millis")
    result
  }
}
