import io.michaelallen.mustache.api.MustacheRenderer
import io.michaelallen.mustache.MustacheFactory

object Test extends App {
  val expectedHtml = """<html>
  <head>
    <title>Hello world - mustache</title>
  </head>
  <body>
    <h1>Hello world</h1>
    <p>Page rendered by Mustache</p>
    <p>This one came from the test folder.</p>
  </body>
</html>
"""
  val fooMustache = MustacheFactory.compile("/foo.mustache")
  val renderer = new MustacheRenderer{}
  val html = renderer.render(
    fooMustache,
    Map(
      "page_title" -> "Hello world - mustache",
      "title" -> "Hello world",
      "message" -> "Page rendered by Mustache"
    )
  )
  assert(html == expectedHtml, "Mustache can be rendered")
}
