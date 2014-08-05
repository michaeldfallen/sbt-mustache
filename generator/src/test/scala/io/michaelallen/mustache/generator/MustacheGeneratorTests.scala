package io.michaelallen.mustache.generator

import io.michaelallen.mustache.test._

class MustacheGeneratorTests extends UnitSpec {
  val generator = new MustacheGenerator{}

  behavior of "MustacheGenerator.templateContent"
  val templateScala = generator.templateContent(
    "example.foo",
    "test",
    "example/foo/test"
  )
  it should "contain the correct package declaration" in {
    templateScala should include("package mustache.example.foo")
  }
  it should "define an object to hold the compiled template" in {
    templateScala should include(
      """object test {
        |  val mustache: Mustache = MustacheFactory.compile("example/foo/test")
        |}""".stripMargin
    )
  }
  it should "define a MustacheTemplate trait which calls the object for a template" in {
    templateScala should include(
      "trait test extends MustacheTemplate {"
    )
    templateScala should include(
      "val mustache: Mustache = test.mustache"
    )
  }

  behavior of "MustacheGenerator.factoryObjectContent"
  it should "override the mustacheDir to look for templates in" in {
    val factoryScala = generator.factoryObjectContent("/foo/bar")
    factoryScala should include("override val mustacheDir = \"/foo/bar\"")
  }
}
