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

  it should "throw FileNotFoundException when mustache file not found" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }
    a [com.github.mustachejava.MustacheException] should be thrownBy {
      compiler.compile("baz")
    }
  }

  it should "find a mustache the wrong file extension given" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }

    val fooMustache = compiler.compile("foo.html")
    fooMustache should not be(null)
  }

  it should "find a mustache without a file extension given" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }

    val fooMustache = compiler.compile("foo")
    fooMustache should not be(null)
  }

  it should "find a partial nested in the same folder" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }
    val a = compiler.compile("/partial/b")
  }

  it should "use default values" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }
    val a = compiler.compile("/partial/a")
  }

  it should "find a .html file" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }
    val test = compiler.compile("test")
  }

  it should "find a file when path starts with /" in {
    val compiler = new MustacheCompiler {
      override val mustacheDir = "mustache"
    }
    val test = compiler.compile("/test")
  }
}
