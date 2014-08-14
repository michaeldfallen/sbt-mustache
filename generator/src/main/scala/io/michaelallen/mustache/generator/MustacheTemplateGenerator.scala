package io.michaelallen.mustache.generator

import java.io.File
import sbt.IO
import sbt.{PathExtra, FileFilter}
import io.michaelallen.logging.Timing

trait MustacheTemplateGenerator extends PathExtra with Timing {

  def copyFiles(sources: Traversable[(File, File)]) = {
    time(s"Copying ${sources.size} templates") {
      IO.copy(sources)
    }
  }

  def copyTemplatesToTarget(
    target: File,
    sourceDirectories: Seq[File],
    includeFilter: FileFilter,
    excludeFilter: FileFilter
  ): Seq[File] = time("Copying Templates") {
    val mappings = sourceDirectories map { dir =>
      val sources = dir ** includeFilter
      val excluded = dir ** excludeFilter
      (sources --- excluded) pair relativeTo(dir)
    }
    val copies = mappings.flatten map {
      case (file, path) => file -> target / path
    }
    copyFiles(copies filter modifiedOrNew)
    copies map (_._2)
  }

  def modifiedOrNew(fileTargetPair: (File, File)):Boolean = {
    val (file, target) = fileTargetPair
    file.newerThan(target)
  }
}

object MustacheTemplateGenerator extends MustacheTemplateGenerator
