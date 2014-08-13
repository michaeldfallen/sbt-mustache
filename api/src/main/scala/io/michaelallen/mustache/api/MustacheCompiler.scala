package io.michaelallen.mustache.api

import scala.util.Random
import java.io.{Reader, StringReader, InputStreamReader, FileNotFoundException}
import com.github.mustachejava.DefaultMustacheFactory
import com.twitter.mustache.ScalaObjectHandler

trait MustacheCompiler {

  type Mustache = io.michaelallen.mustache.api.Mustache

  val mustacheDir: String = ""

  private def getResource(str: String) = Option(getClass.getResourceAsStream(str))

  private def notFound(name: String) = {
    throw new FileNotFoundException(s"Failed to find $name. Tried $name, $name.mustache and $name.html")
  }

  private[api] lazy val mustacheFactory = {
    val factory = new DefaultMustacheFactory {
      override def getReader(resourcePath: String): Reader = {
        val resourceName = resourcePath.stripSuffix(".html").stripSuffix(".mustache")

        def resource = getResource(s"/$mustacheDir/$resourceName")
        def html = getResource(s"/$mustacheDir/$resourceName.html")
        def mustache = getResource(s"/$mustacheDir/$resourceName.mustache")

        val stream = resource orElse html orElse mustache
        val reader = new InputStreamReader(stream getOrElse notFound(resourceName))
        reader
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
