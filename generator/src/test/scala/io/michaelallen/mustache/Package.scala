package io.michaelallen.mustache

package object test {
  abstract class UnitSpec
    extends org.scalatest.FlatSpec
    with org.scalatest.Matchers
    with org.scalatest.OptionValues
    with org.scalatest.Inside
    with org.scalatest.Inspectors
}
