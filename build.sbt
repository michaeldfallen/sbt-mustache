import bintray.Keys._

lazy val `sbt-mustache` = project
  .in(file("."))
  .aggregate(generator, api, plugin)
  .settings(commonSettings:_*)
  .settings(crossScala:_*)
  .settings(noPublish:_*)

//Compiles and Renders Mustache html templates
lazy val api = project
  .in(file("api"))
  .settings(commonSettings: _*)
  .settings(crossScala: _*)
  .settings(publishMaven: _*)
  .settings(
    name := "sbt-mustache-api",
    libraryDependencies ++= Seq(
      scalaTest(scalaBinaryVersion.value),
      logger(),
      "com.github.spullara.mustache.java" % "compiler" % "0.8.15"
    ),
    initLoggerInTests()
  )

//Handles generation of Scala files from Mustache html templates
lazy val generator = project
  .in(file("generator"))
  .dependsOn(api)
  .settings(commonSettings: _*)
  .settings(crossScala: _*)
  .settings(publishMaven: _*)
  .settings(
    name := "sbt-mustache-generator",
    libraryDependencies ++= Seq(
      scalaCompiler(scalaVersion.value),
      scalaTest(scalaBinaryVersion.value),
      logger(),
      "org.scala-sbt" % "io" % "0.13.5"
    ),
    fork in run := true,
    initLoggerInTests()
  )

lazy val plugin = project
  .in(file("plugin"))
  .dependsOn(generator)
  .settings(commonSettings: _*)
  .settings(crossScala: _*)
  .settings(publishSbtPlugin: _*)
  .settings(scriptedSettings: _*)
  .settings(
    name := "sbt-mustache",
    scriptedLaunchOpts += ("-Dproject.version=" + version.value),
    scriptedLaunchOpts += "-XX:MaxPermSize=256m",
    scriptedBufferLog := false,
    sbtPlugin := true,
    resourceGenerators in Compile <+= generateVersionFile
  )

def commonSettings = {
  Seq(
    organization := "io.michaelallen.mustache",
    version := "0.3-SNAPSHOT",
    scalaVersion := sys.props.get("scala.version").getOrElse("2.10.4"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    resolvers += Resolver.mavenLocal
  )
}

def noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishTo := Some(Resolver.file("no-publish", crossTarget.value / "no-publish"))
)

def publishSbtPlugin = bintrayPublishSettings ++ Seq(
  publishMavenStyle := false,
  repository in bintray := "sbt-plugins",
  bintrayOrganization in bintray := None,
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))
)

def publishMaven = bintrayPublishSettings ++ Seq(
  publishMavenStyle := true,
  repository in bintray := "maven",
  bintrayOrganization in bintray := None,
  homepage := Some(url("https://github.com/michaeldfallen/sbt-mustache")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  pomExtra := {
    <scm>
      <url>https://github.com/michaeldfallen/sbt-mustache</url>
      <connection>scm:git:git@github.com:michaeldfallen/sbt-mustache.git</connection>
    </scm>
    <developers>
      <developer>
        <id>michaeldfallen</id>
        <name>Michael Allen</name>
        <url>https://github.com/michaeldfallen</url>
      </developer>
    </developers>
  },
  pomIncludeRepository := { _ => false }
)

def generateVersionFile = Def.task {
  val version = (Keys.version in api).value
  val file = (resourceManaged in Compile).value / "mustache.version.properties"
  val content = s"mustache.api.version=$version"
  IO.write(file, content)
  Seq(file)
}

def crossScala = Seq(
  crossScalaVersions := Seq("2.10.4"),
  unmanagedSourceDirectories in Compile += {
    (sourceDirectory in Compile).value / ("scala-" + scalaBinaryVersion.value)
  }
)

def scalaCompiler(version: String) = {
  "org.scala-lang" % "scala-compiler" % version
}

def scalaTest(version: String) = version match {
  case "2.10" => "org.scalatest" %% "scalatest" % "2.2.0" % "test"
}

def logger() = {
  "ch.qos.logback" % "logback-classic" % "1.0.1"
}

def initLoggerInTests() = {
  testOptions in Test += Tests.Setup(classLoader =>
    classLoader
      .loadClass("org.slf4j.LoggerFactory")
      .getMethod("getLogger", classLoader.loadClass("java.lang.String"))
      .invoke(null, "ROOT")
  )
}
