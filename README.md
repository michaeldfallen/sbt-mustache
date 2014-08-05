#SBT {{mustache}} ![build-status]

 [build-status]: https://travis-ci.org/michaeldfallen/sbt-mustache.svg?branch=master

An SBT plugin for integrating Mustache templates into Scala projects.

We take the same philosophy as the Play Frameworks [Twirl] plugin, generating
Scala sources that are preconfigured to access your templates during compile.

This plugin was inspired by [@julienba]'s [play2-mustache] plugin, which we used
on the [Register to vote] exemplar project. Sadly since Play migrated to
[SBT-Web], in it's recent [play-2.3], the [play2-mustache] plugin has been
deprecated.

##Work in progress

This plugin is a work in progress. Currently you can checkout the code, build it
, publish it locally and make use of it to do basic Mustache compilation and
rendering.

####Working
- Copy `.mustache` files from the `src/mustache` directory into the classpath
- Generate a `MusacheFactory` object that can compile your mustache files from
  the classpath

####To do

#####Generate objects for each mustache file that knows how to access the template

If I have a file at `src/mustache/test/myTemplate.mustache` there should
be a Scala object at `mustace.test.myTemplate` that knows how to render the
template.

#####Generate traits that shadow the mustache objects so we can mix them in to presenters

If we have the object above then we should be able to define a class that
extends a trait that calls of to the object:

```
case class MyTemplate (foo: String) extends mustache.test.myTemplate
```

This case class then represents the model needed to render the template and once
newed up presents a `.render()` function to generate the html.

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
