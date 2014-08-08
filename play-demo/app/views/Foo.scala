package views

case class Foo(
  page_title: String = "Hello World - Mustache",
  title: String = "Hello World",
  message: String = ""
) extends mustache.foo
