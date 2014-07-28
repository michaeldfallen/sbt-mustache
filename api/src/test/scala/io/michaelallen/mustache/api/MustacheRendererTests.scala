package io.michaelallen.mustache.api

import io.michaelallen.mustache.test._

class MustacheRendererTests extends UnitSpec {
  val renderer = new MustacheRenderer {}
  val compiler = new MustacheCompiler {}

  val mustache = compiler.compile("""{{foo}}""", "foo")

  "MustacheRenderer.render" should "render a simple template" in {
    val html = renderer.render(mustache, Map("foo" -> "I am Foo"))
    html should be("I am Foo")
  }

  it should "render from an annonymous class" in {
    val html = renderer.render(mustache, new {
      val foo = "Foo from annonymous class"
    })
    html should be("Foo from annonymous class")
  }
}
