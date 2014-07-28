package io.michaelallen.mustache.generator

import java.io.File
import io.michaelallen.mustache.api._

class MustacheGenerator {

  val compiler = new MustacheCompiler {}

  def generateSource (
      relativePath: List[String],
      name: String,
      template: String
  ): String = {
    s"""
      package ${relativePath.mkString(".")}
      class $name {
        val template: String = "$template"
      }
    """
  }
}
