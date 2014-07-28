lazy val `sbt-mustache` = project
  .in(file("."))
  .aggregate(generator, api)
  .settings(commonSettings:_*)
  .settings(crossScala:_*)
  .settings(noPublish:_*)

//Compiles and Renders Mustache html templates
lazy val api = project
  .in(file("api"))
  .settings(commonSettings: _*)
  .settings(crossScala: _*)
  .settings(
    name := "sbt-mustache-api",
    libraryDependencies ++= Seq(
      scalaTest,
      "com.github.spullara.mustache.java" % "compiler" % "0.8.15"
    )
  )

//Handles generation of Scala files from Mustache html templates
lazy val generator = project
  .in(file("generator"))
  .dependsOn(api % "compile;test->test")
  .settings(commonSettings: _*)
  .settings(crossScala: _*)
  .settings(
    name := "sbt-mustache-generator",
    libraryDependencies ++= Seq(
      scalaCompiler(scalaVersion.value),
      scalaTest
    ),
    fork in run := true
  )

def commonSettings = {
  Seq(
    organization := "io.michaelallen.mustache",
    version := "0.1-SNAPSHOT",
    scalaVersion := sys.props.get("scala.version").getOrElse("2.10.4"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
  )
}

def noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishTo := Some(Resolver.file("no-publish", crossTarget.value / "no-publish"))
)

def crossScala = Seq(
//  crossScalaVersions := Seq("2.9.3", "2.10.4", "2.11.1"),
  crossScalaVersions := Seq("2.10.4"),
  unmanagedSourceDirectories in Compile += {
    (sourceDirectory in Compile).value / ("scala-" + scalaBinaryVersion.value)
  }
)

def scalaCompiler(version: String) = {
  "org.scala-lang" % "scala-compiler" % version
}

def scalaTest = {
  "org.scalatest" %% "scalatest" % "2.2.0" % "test"
}
