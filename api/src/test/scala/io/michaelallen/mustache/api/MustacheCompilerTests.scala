package io.michaelallen.mustache.api

import io.michaelallen.mustache.test._
import java.io.{Reader, StringReader}

class MustacheCompilerTests extends UnitSpec {

  "MustacheRenderer" should "compile a mustache" in {
    val compiler = new MustacheCompiler {}
    val mustache = compiler.compile("""{{foo}}""", "test")
    mustache should not be(null)
  }

  it should "compile an already recognised mustache from ref" in {
    val compiler = new MustacheCompiler {
      override val readFile: String => Reader = {
        case "foo" => new StringReader("""{{foo}}""")
        case "bar" => new StringReader("""{{bar}}""")
      }
    }

    val fooMustache = compiler.compile("foo")
    val barMustache = compiler.compile("bar")
    fooMustache should not be(barMustache)
  }
}
