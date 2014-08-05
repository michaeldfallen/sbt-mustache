package io.michaelallen.mustache.generator

import io.michaelallen.mustache.test._

class MustacheGeneratorTests extends UnitSpec {
  val generator = MustacheGenerator

  behavior of "MustacheGenerator.generateSourceForTemplate"
  val templateScala = generator.generateSourceForTemplate(
    List("mustache", "example", "foo"),
    "test",
    "example/foo/test"
  )
  it should "contain the correct package declaration" in {
    templateScala should include("package mustache.example.foo")
  }
  it should "have the correct class name and extend MustacheTemplate" in {
    templateScala should include(
      "object test extends MustacheTemplate {"
    )
  }
  it should "call to the MustacheFactory to get it's template" in {
    templateScala should include(
      "val mustache: Mustache = MustacheFactory.compile(\"example/foo/test\")"
    )
  }
}
