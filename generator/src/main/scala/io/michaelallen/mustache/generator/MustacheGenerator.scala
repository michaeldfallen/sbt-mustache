package io.michaelallen.mustache.generator

import java.io.File
import sbt.IO
import sbt.PathExtra
import io.michaelallen.mustache.api._

object MustacheGenerator extends PathExtra {

  def generateSourceForTemplate(
      relativePath: List[String],
      name: String,
      template: String
  ): String = {
    s"""
      package ${relativePath.mkString(".")}

      import io.michaelallen.mustache.api.MustacheTemplate
      import io.michaelallen.mustache.MustacheFactory

      object $name extends MustacheTemplate {
        val mustache: Mustache = MustacheFactory.compile("$template")
      }
    """
  }

  def generateFactoryObject(
      targetDir: File,
      mustacheTarget: String
  ): File = {
    val file = targetDir / "io" / "michaelallen" / "mustache" / "MustacheFactory.scala"
    val content = factoryObjectContent(mustacheTarget)
    IO.write(file, content)
    file
  }

  def factoryObjectContent(
      mustacheTarget: String
  ): String = {
    s"""
      package io.michaelallen.mustache

      import io.michaelallen.mustache.api.MustacheCompiler
      import java.io.InputStreamReader

      object MustacheFactory extends MustacheCompiler {
        override val mustacheDir = "$mustacheTarget"
      }
     """
  }
}
