package io.michaelallen.mustache.sbt

import sbt._
import sbt.Keys._
import com.typesafe.sbt.web.SbtWeb

object Import {
  object MustacheKeys {
    val mustache = TaskKey[Seq[File]]("mustache", "Generates the Mustache source files for accessing Mustaches from")
  }
}

object SbtMustache extends AutoPlugin {
  val autoImport = Import
  import autoImport.MustacheKeys._
  import SbtWeb.autoImport._
  import WebKeys._

  override def requires = SbtWeb && plugins.JvmPlugin

  override def trigger  = AllRequirements

  override def projectSettings = {
    inConfig(Compile)(mustacheSettings) ++
    inConfig(Test)(mustacheSettings) ++
    defaultSettings ++
    dependencySettings
  }

  def defaultSettings = Seq(
  )

  def mustacheSettings = Seq(
    mustache := {
      val file = (sourceManaged).value / "demo" / "Mustache.scala"
      IO.write(file, """object Mustache { def test = "Hi" }""")
      Seq(file)
    },
    sourceGenerators <+= mustache
  )

  def generatorSettings = Seq(
  )

  def dependencySettings: Seq[Setting[_]] = Seq(
    libraryDependencies += "io.michaelallen.mustache" %% "sbt-mustache-api" % version.value
  )
}
