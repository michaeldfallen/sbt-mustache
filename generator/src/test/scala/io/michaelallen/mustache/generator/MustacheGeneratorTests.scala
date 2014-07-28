package io.michaelallen.mustache.generator

import io.michaelallen.mustache.test._

class MustacheGeneratorTests extends UnitSpec {
  val generator = new MustacheGenerator()

  behavior of "MustacheGenerator.generateSource"
  val scala = generator.generateSource(
    List("mustache", "example", "foo"),
    "test",
    "example/foo/test"
  )
  it should "contain the correct package declaration" in {
    scala should include("package mustache.example.foo")
  }
  it should "have the correct class name" in {
    scala should include("class test {")
  }
  it should "declare the correct template" in {
    scala should include("val template: String = \"example/foo/test\"")
  }
}
