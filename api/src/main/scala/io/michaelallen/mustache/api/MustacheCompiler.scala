package io.michaelallen.mustache.api

import scala.util.Random
import java.io.{Reader, StringReader, InputStreamReader}
import com.github.mustachejava.DefaultMustacheFactory
import com.twitter.mustache.ScalaObjectHandler

trait MustacheCompiler {

  type Mustache = io.michaelallen.mustache.api.Mustache

  val readFile: String => Reader = { (filename) => new StringReader("") }

  private[api] lazy val mustacheFactory = {
    val factory = new DefaultMustacheFactory {
      override def getReader(resourceName: String): Reader = {
        readFile(resourceName)
      }
    }
    factory.setObjectHandler(new ScalaObjectHandler)
    factory
  }

  def compile(template:String): Mustache = {
    new Mustache(mustacheFactory.compile(template))
  }

  def compile(
      template: String,
      name: String
  ): Mustache = {
    new Mustache(mustacheFactory.compile(
      new StringReader(template),
      name
    ))
  }
}
