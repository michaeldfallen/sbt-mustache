lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .settings(MustacheKeys.playSupport := true)
  .settings(
    scalaSource in Test := baseDirectory.value / "test-src"
  )
