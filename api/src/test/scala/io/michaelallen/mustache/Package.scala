package io.michaelallen.mustache

import org.scalatest.matchers._
import scala.reflect._

package object test {
  abstract class UnitSpec
    extends org.scalatest.FlatSpec
    with org.scalatest.Matchers
    with org.scalatest.OptionValues
    with org.scalatest.Inside
    with org.scalatest.Inspectors

  def ofType[T:ClassTag] = BeMatcher { obj: Any =>
    val cls = classTag[T].runtimeClass
    MatchResult(
      obj.getClass == cls,
      obj.toString + " was not an instance of " + cls.toString,
      obj.toString + " was an instance of " + cls.toString
    )
  }

  def anInstanceOf[T:ClassTag] = BeMatcher { obj: Any =>
    val cls = classTag[T].runtimeClass
    MatchResult(
      cls.isAssignableFrom(obj.getClass),
      obj.getClass.toString + " was not assignable from " + cls.toString,
      obj.getClass.toString + " was assignable from " + cls.toString
    )
  }
}
