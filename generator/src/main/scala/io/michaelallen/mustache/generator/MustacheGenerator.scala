package io.michaelallen.mustache.generator

import java.io.File
import sbt.IO
import sbt.{PathExtra, FileFilter}

trait MustacheGenerator extends PathExtra {

  def writeFile(file:File, content:String) = IO.write(file, content)

  def templateContent(
      namespace: String,
      name: String,
      template: String
  ): String = {
    s"""
      |package mustache.${namespace}
      |import io.michaelallen.mustache.api.MustacheTemplate
      |import io.michaelallen.mustache.api.Mustache
      |import io.michaelallen.mustache.MustacheFactory
      |
      |object $name {
      |  val mustache: Mustache = MustacheFactory.compile("$template")
      |}
      |
      |trait $name extends MustacheTemplate {
      |  val mustache: Mustache = $name.mustache
      |}
      |""".stripMargin
  }

  def factoryObjectContent(
      mustacheTarget: String
  ): String = {
    s"""
      |package io.michaelallen.mustache
      |
      |import io.michaelallen.mustache.api.MustacheCompiler
      |import java.io.InputStreamReader
      |
      |object MustacheFactory extends MustacheCompiler {
      |  override val mustacheDir = "$mustacheTarget"
      |}
      |""".stripMargin
  }

  def generateTemplateSourcesForDirectory(
      directory: File,
      sourceTarget: File,
      includeFilter: FileFilter,
      excludeFilter: FileFilter
  ): Seq[File] = {
    val included = directory ** includeFilter
    val excluded = directory ** excludeFilter
    val templates = included --- excluded
    val mappings = templates pair relativeTo(directory)
    mappings map { case (file, path) =>
      val name = file.base
      val ext = file.ext
      val template = path
      val relativePath = new File(path).getParent
      val namespace = relativePath.replaceAll("/", ".")

      val targetFile = sourceTarget / "mustache" / relativePath / s"$name.scala"
      val content = templateContent(namespace, name, template)
      writeFile(targetFile, content)
      targetFile
    }
  }

  def generateSources(
      mustacheTargetPrefix: String,
      sourceTarget: File,
      sourceDirectories: Seq[File],
      includeFilter: FileFilter,
      excludeFilter: FileFilter
  ): Seq[File] = {
    val sourcesInSeqs = sourceDirectories map { directory =>
      generateTemplateSourcesForDirectory(
        directory, sourceTarget, includeFilter, excludeFilter
      )
    }
    val factorySource = generateFactoryObject(
      sourceTarget,
      mustacheTargetPrefix
    )
    sourcesInSeqs.flatten :+ factorySource
  }

  def generateFactoryObject(
      targetDir: File,
      mustacheTarget: String
  ): File = {
    val file = targetDir / "io" / "michaelallen" / "mustache" / "MustacheFactory.scala"
    val content = factoryObjectContent(mustacheTarget)
    writeFile(file, content)
    file
  }
}

object MustacheGenerator extends MustacheGenerator
