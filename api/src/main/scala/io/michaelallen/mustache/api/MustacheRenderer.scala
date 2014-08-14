package io.michaelallen.mustache.api

import java.io.StringWriter
import io.michaelallen.logging.Timing

trait MustacheRenderer extends Timing {
  def render(
      template: Mustache,
      data: Any
  ): String = time(s"Rendering $template") {
    val writer = new StringWriter()
    template.execute(writer, data).flush()
    writer.close()

    writer.toString()
  }
}

