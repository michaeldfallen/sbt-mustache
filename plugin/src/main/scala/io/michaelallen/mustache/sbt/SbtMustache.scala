package io.michaelallen.mustache.sbt

import sbt._
import sbt.Keys._
import com.typesafe.sbt.web.SbtWeb
import io.michaelallen.mustache.generator.MustacheGenerator

object Import {
  object MustacheKeys {
    val mustacheTemplate = TaskKey[Seq[File]]("mustache-template", "Load Mustache templates")
    val mustache = TaskKey[Seq[File]]("mustache", "Generates the Scal source files for accessing Mustaches from")
  }
}

object SbtMustache extends AutoPlugin {
  val autoImport = Import
  import autoImport.MustacheKeys._
  import SbtWeb.autoImport._
  import WebKeys._

  override def requires = plugins.JvmPlugin && SbtWeb

  override def trigger  = AllRequirements

  override def projectSettings = {
    inConfig(Compile)(mustacheTemplateSettings) ++
    inConfig(Test)(mustacheTemplateSettings) ++
    inConfig(Compile)(mustacheSettings) ++
    inConfig(Test)(mustacheSettings) ++
    defaultSettings ++
    dependencySettings
  }

  def defaultSettings = Seq(
  )

  def mustacheTemplateSettings = Seq(
    includeFilter in mustacheTemplate := "*.mustache",
    excludeFilter in mustacheTemplate := HiddenFileFilter,
    sourceDirectories in mustacheTemplate := Seq(sourceDirectory.value / "mustache"),
    target in mustacheTemplate := resourceManaged.value / "mustache",
    mustacheTemplate := {
      val includeFileFilter = (includeFilter in mustacheTemplate).value
      val excludeFileFilter = (excludeFilter in mustacheTemplate).value
      val sourceDirs = (sourceDirectories in mustacheTemplate).value
      val targetDir = (target in mustacheTemplate).value

      val mappings = sourceDirs map { dir =>
        val sources = dir ** includeFileFilter
        val excluded = dir ** excludeFileFilter
        (sources --- excluded) pair relativeTo(dir)
      }
      val copies = mappings.flatten map {
        case (file, path) => file -> targetDir / path
      }
      IO.copy(copies)
      copies map (_._2)
    },
    resourceGenerators += mustacheTemplate.taskValue
  )

  def mustacheSettings = Seq(
    mustache := {
      //Get the prefix from resourceManaged so the classloader knows where to access mustaches
      val (_, mustacheTargetPrefix) = ((target in mustacheTemplate).value pair relativeTo(resourceManaged.value)).head
      val factoryObjectFile = MustacheGenerator.generateFactoryObject(sourceManaged.value, mustacheTargetPrefix)
      Seq(factoryObjectFile)
    },
    mustache <<= mustache dependsOn mustacheTemplate,
    sourceGenerators <+= mustache
  )

  def dependencySettings: Seq[Setting[_]] = Seq(
    libraryDependencies += "io.michaelallen.mustache" %% "sbt-mustache-api" % version.value
  )
}
