#SBT {{mustache}}

[![Build Status](https://travis-ci.org/michaeldfallen/sbt-mustache.svg?branch=master)](https://travis-ci.org/michaeldfallen/sbt-mustache)

An SBT plugin for integrating Mustache templates into Scala projects.

We take the same philosophy as the Play Frameworks [Twirl] plugin, generating
Scala sources that are preconfigured to access your templates during compile.

This plugin was inspired by [@julienba]'s [play2-mustache] plugin, which we used
on the [Register to vote] exemplar project. Sadly since Play migrated to
[SBT-Web], in it's recent [play-2.3], the [play2-mustache] plugin has been
deprecated.

##Installation

####Build the source

I will be publishing version 0.1 very soon. For now if you want to use the plugin
you will need to compile it from source first.

```
> git clone git@github.com:michaeldfallen/sbt-mustache.git
> cd sbt-mustache
> sbt compile
> sbt publish-local
```

That will put version 0.1-SNAPSHOT of the plugin into your local ivy cache.

**Note**: If you use a wrapper around `sbt` it may change your `ivy.home` (Play
will often do this). You need to ensure that the `ivy.home` used by `sbt` when
you build the plugin is the same `ivy.home` when you use it, otherwise you'll
fail to resolve the artifact.

####Install into project

To your projects `plugins.sbt` add the following:

```
addSbtPlugin("io.michaelallen.mustache" % "sbt-mustache" % "0.1-SNAPSHOT")
```

Done. SBT 0.13 added AutoPlugins which allows plugins to handle their default
configuration themselves.

##Usage

There's two ways to use this plugin, which I'll call *The Hogan way* and *The
Twirl way*. Whichever you choose is up to you.

####Source Directories

Mustache templates are stored in `src/main/mustache`. Place your templates here.

If you would like to add further directories to look for Mustache templates you
can edit the key `sourceDirectories in mustacheTemplate`.

Adding a new source directory for Mustache templates:
```
lazy val root = (project in file(".")).settings(
  sourceDirectories in mustacheTemplate :+ baseDirectory / "mySpecialMustaches"
)
```

####The Hogan way

In Hogan.js you call off to a Mustache compiler to compile your template then
render that template with a set of data. This can be done in Sbt-Mustache.

On compile Sbt-Mustache will generate a few source files. One of them is the
`io.michaelallen.mustache.MustacheFactory` object, which is configured to look
for the Mustache templates in your source directories.

You can simply call `MustacheFactory.compile` to ask the factory to compile you
a template, like you would in Hogan.js

With a template sitting in `src/main/mustache/foo/bar.mustache`:
```
<h1>{{message}}</h1>
```

We can ask the MustacheFactory to compile it:

```
val template = MustacheFactory.compile("foo/bar.mustache")
```

Then execute the template to render it's html:

```
val writer = new StringWriter()
template.execute(writer, Map("message" -> "Hello World!"))

writer.flush().toString == "<h1>Hello World!</h1>"
```

####The Twirl way

Twirl models your templates as Scala files, by generating Scala objects that
understand how to render the html of the template.

This is a nice feature and something I wanted to bring to Sbt-Mustache.

If you have a template at `src/main/mustache/foo/bar.mustache`:
```
<h1>{{message}}</h1>
```

Then Sbt-Mustache will generate a Scala trait at `mustache.foo.bar` which
understands how to render the `bar.mustache` template.

We can then mix that trait into a class or case class to provide the backing
object to render based off:

```
case class Bar(message:String) extends mustache.foo.bar
```

Then newing that class up and calling render will generate our html:

```
Bar(message = "Hello World!").render == "<h1>Hello World!</h1>"
```

##Work in progress

This plugin is a work in progress. Currently you can checkout the code, build it
, publish it locally and make use of it to do basic Mustache compilation and
rendering.

####Working
- Copy `.mustache` files from the `src/mustache` directory into the classpath
- Generate a `MusacheFactory` object that can compile your mustache files from
  the classpath
- Generate a Mustache Template object for each `*.mustache` file in the
  `src/mustache` directory that provides access a compiled form of the template
- Generate a Mustache Template trait for each Mustache object that knows how to
  render the template

####To do

#####Provide the mustache traits with a Play Framework compatible content type

[Twirl] provides templates in such a way that they can be returned as the
content of a Play Result

```
def foo = Action {
  Ok(views.html.foo())
}
```

We could do the same with mustaches

```
def foo = Action {
  Ok(MyTemplate())
}
```

 [SBT-Web]: https://github.com/sbt/sbt-web
 [Twirl]: https://github.com/playframework/twirl
 [@julienba]: https://github.com/julienba
 [play2-mustache]: https://github.com/julienba/play2-mustache
 [Register to vote]: https://www.gov.uk/transformation/register-to-vote
 [play-2.3]: http://www.playframework.com/documentation/2.3.x/Highlights23