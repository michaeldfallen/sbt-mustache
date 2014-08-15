package io.michaelallen.mustache.api

import scala.util.Random
import java.io.{Reader, StringReader, InputStreamReader, FileNotFoundException}
import com.github.mustachejava.DefaultMustacheFactory
import com.twitter.mustache.ScalaObjectHandler
import io.michaelallen.logging.Timing

trait MustacheCompiler extends Timing {

  type Mustache = io.michaelallen.mustache.api.Mustache

  val mustacheDir: String = ""

  private def getResourceFromResources(str: String) = Option(getClass.getResourceAsStream(str))
  private def getResourceFromJar(str: String) = Option(getClass.getClassLoader.getResourceAsStream(str))
  private def getResource(str: String) = getResourceFromResources(str) orElse getResourceFromJar(str)

  private def notFound(name: String) = {
    throw new FileNotFoundException(s"Failed to find $name. Tried $name, $name.mustache and $name.html")
  }

  private[api] lazy val mustacheFactory = {
    val factory = new DefaultMustacheFactory {
      override def getReader(resource: String): Reader = time(s"GetReader $resource") {
        val resourcePath = if (resource.startsWith("/")) {
          s"$mustacheDir$resource"
        } else {
          s"$mustacheDir/$resource"
        }
        val resourceNoExt = resourcePath.stripSuffix(".html").stripSuffix(".mustache")

        def base = getResource(s"$resourcePath")
        def html = getResource(s"$resourceNoExt.html")
        def mustache = getResource(s"$resourceNoExt.mustache")

        val stream = base orElse mustache orElse html
        val reader = new InputStreamReader(stream getOrElse notFound(resourcePath))
        reader
      }
    }
    factory.setObjectHandler(new ScalaObjectHandler)
    factory
  }

  def compile(template:String): Mustache = time(s"Compiling $template") {
    new Mustache(mustacheFactory.compile(template))
  }

  def compile(
      template: String,
      name: String
  ): Mustache = time(s"Compiling $template") {
    new Mustache(mustacheFactory.compile(
      new StringReader(template),
      name
    ))
  }
}
