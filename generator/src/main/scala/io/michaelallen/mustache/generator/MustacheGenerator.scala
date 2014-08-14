package io.michaelallen.mustache.generator

import java.io.File
import sbt.IO
import sbt.{PathExtra, FileFilter}
import io.michaelallen.logging.Timing

trait MustacheGenerator extends PathExtra with Timing {

  def writeFile(file:File, content:String) = time(s"writing to ${file.name}") {
    IO.write(file, content)
  }

  def templateContent(
      namespace: Seq[String],
      name: String,
      template: String,
      templateHash: String
  ): String = {
    s"""
      |package ${namespace.mkString(".")}
      |import io.michaelallen.mustache.api.MustacheTemplate
      |import io.michaelallen.mustache.api.Mustache
      |import io.michaelallen.mustache.MustacheFactory
      |
      |object $name {
      |  val hash: String = "$templateHash"
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

  def playImplicitsContent(): String = {
    s"""
      |package io.michaelallen.mustache
      |
      |import play.api.http.{ContentTypeOf, MimeTypes}
      |import io.michaelallen.mustache.api.MustacheTemplate
      |import play.api.http.Writeable
      |import play.api.mvc.Codec
      |
      |trait PlayImplicits {
      |  implicit def mustacheContentType: ContentTypeOf[MustacheTemplate] = {
      |    ContentTypeOf(Some(MimeTypes.HTML))
      |  }
      |  implicit def writableMustache(implicit codec: Codec): Writeable[MustacheTemplate] = {
      |    Writeable[MustacheTemplate](
      |      (result: MustacheTemplate) => codec.encode(result.render())
      |    )
      |  }
      |}
      |object PlayImplicits extends PlayImplicits
      |""".stripMargin
  }

  def generatePlaySource(sourceTarget: File): File = {
    val file = sourceTarget / "io" / "michaelallen" / "mustache" / "PlayImplicits.scala"
    if (!file.exists) {
      val content = playImplicitsContent()
      writeFile(file, content)
    }
    file
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
    mappings.foldLeft(Seq.empty[File]) { case (files, (file, path)) =>
      val name = file.base
      val ext = file.ext
      val template = path
      val templateHash = file.hashString

      val parentPath = Option(new File(path).getParent)
      val namespace = "mustache" +: parentPath.map(_.split("/")).toSeq.flatten
      val relativePath = namespace.mkString("/")

      val targetFile = sourceTarget / relativePath / s"$name.scala"
      if (!targetFile.exists || targetFile.olderThan(file)) {
        val content = templateContent(namespace, name, template, templateHash)
        writeFile(targetFile, content)
      }
      files :+ targetFile
    }
  }

  def generateSources(
      mustacheTargetPrefix: String,
      sourceTarget: File,
      sourceDirectories: Seq[File],
      includeFilter: FileFilter,
      excludeFilter: FileFilter,
      createPlayImplicits: Boolean
  ): Seq[File] = time("Generate Scala sources") {
    val sourcesInSeqs = sourceDirectories map { directory =>
      generateTemplateSourcesForDirectory(
        directory, sourceTarget, includeFilter, excludeFilter
      )
    }
    val factorySource = generateFactoryObject(
      sourceTarget,
      mustacheTargetPrefix
    )
    val playSource = if(createPlayImplicits) {
      Seq(generatePlaySource(sourceTarget))
    } else {
      Seq.empty[File]
    }
    sourcesInSeqs.flatten ++ playSource :+ factorySource
  }

  def generateFactoryObject(
      targetDir: File,
      mustacheTarget: String
  ): File = {
    val file = targetDir / "io" / "michaelallen" / "mustache" / "MustacheFactory.scala"
    if (!file.exists) {
      val content = factoryObjectContent(mustacheTarget)
      writeFile(file, content)
    }
    file
  }
}

object MustacheGenerator extends MustacheGenerator
