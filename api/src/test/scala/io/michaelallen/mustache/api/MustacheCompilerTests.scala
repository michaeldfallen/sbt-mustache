package io.michaelallen.mustache.api

import io.michaelallen.mustache.test._
import java.io.{Reader, StringReader}

class MustacheCompilerTests extends UnitSpec {

  "MustacheRenderer" should "compile a mustache" in {
    val compiler = new MustacheCompiler {}
    val mustache = compiler.compile("""{{foo}}""", "test")
    mustache should not be(null)
  }

  it should "compile a mustache from ref on the classpath" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }

    val fooMustache = compiler.compile("foo.mustache")
    val barMustache = compiler.compile("bar.mustache")
    fooMustache should not be(barMustache)
  }
}
