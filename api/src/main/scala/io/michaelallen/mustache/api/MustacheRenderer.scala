package io.michaelallen.mustache.api

import java.io.StringWriter

trait MustacheRenderer {
  def render(
      template: Mustache,
      data: Any
  ): String = {
    val writer = new StringWriter()
    template.execute(writer, data).flush()
    writer.close()

    writer.toString()
  }
}

