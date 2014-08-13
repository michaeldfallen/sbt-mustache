resolvers += Resolver.url(
  "bintray-sbt-plugin-michaelallen",
   url("https://dl.bintray.com/michaelallen/sbt-plugins/"))(
       Resolver.ivyStylePatterns)

resolvers += "bintray-maven-michaelallen" at "https://dl.bintray.com/michaelallen/maven/"

addSbtPlugin("io.michaelallen.mustache" %% "sbt-mustache" % sys.props("project.version"))
