package io.michaelallen.mustache.api

import io.michaelallen.mustache.test._

class MustacheTemplateTests extends UnitSpec {
  val compiler = new MustacheCompiler{}

  "MustacheTemplate" should "be able to render itself" in {
    val template = new MustacheTemplate {
      val foo = "John"
      val bar = "Smith"
      val mustache = compiler.compile("{{foo}}, {{bar}}", "test")
    }

    template.render() should be("John, Smith")
  }
}
