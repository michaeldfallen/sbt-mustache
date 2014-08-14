package io.michaelallen.mustache.sbt

import sbt._
import sbt.Keys._
import io.michaelallen.mustache.generator.{
  MustacheGenerator,
  MustacheTemplateGenerator
}

object Import {
  object MustacheKeys {
    val mustacheTemplate = TaskKey[Seq[File]]("mustache-template", "Load Mustache templates")
    val mustache = TaskKey[Seq[File]]("mustache", "Generates the Scal source files for accessing Mustaches from")
    val playSupport = SettingKey[Boolean]("play-support", "Control whether you need Play content types support")
    val apiVersion = SettingKey[String]("mustache-version", "The version of sbt-mustache-api to use")
  }
}

object SbtMustache extends AutoPlugin {
  val autoImport = Import
  import autoImport.MustacheKeys._

  override def requires = plugins.JvmPlugin

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
    playSupport := false
  )

  def mustacheTemplateSettings = Seq(
    includeFilter in mustacheTemplate := "*.mustache" | "*.html",
    excludeFilter in mustacheTemplate := HiddenFileFilter,
    sourceDirectories in mustacheTemplate := Seq(sourceDirectory.value / "mustache"),
    target in mustacheTemplate := resourceManaged.value / "mustache",
    mustacheTemplate := {
      MustacheTemplateGenerator.copyTemplatesToTarget(
        (target in mustacheTemplate).value,
        (sourceDirectories in mustacheTemplate).value,
        (includeFilter in mustacheTemplate).value,
        (excludeFilter in mustacheTemplate).value
      )
    },
    resourceGenerators <+= mustacheTemplate
  )

  def mustacheSettings = Seq(
    target in mustache := sourceManaged.value,
    mustache := {
      MustacheGenerator.generateSources(
        "mustache",
        (target in mustache).value,
        (sourceDirectories in mustacheTemplate).value,
        (includeFilter in mustacheTemplate).value,
        (excludeFilter in mustacheTemplate).value,
        (playSupport).value
      )
    },
    sourceGenerators <+= mustache
  )

  def dependencySettings: Seq[Setting[_]] = Seq(
    apiVersion := readResourceProperty("mustache.version.properties", "mustache.api.version"),
    libraryDependencies += "io.michaelallen.mustache" %% "sbt-mustache-api" % apiVersion.value
  )

  def readResourceProperty(resource: String, property: String): String = {
    val props = new java.util.Properties
    val stream = getClass.getClassLoader.getResourceAsStream(resource)
    try { props.load(stream) }
    catch { case e: Exception => }
    finally { if (stream ne null) stream.close }
    props.getProperty(property)
  }
}
