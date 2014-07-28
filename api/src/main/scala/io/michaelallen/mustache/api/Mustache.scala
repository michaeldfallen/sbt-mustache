package io.michaelallen.mustache.api

import com.github.mustachejava.{Mustache => JavaMustache}
import java.io.Writer

/*
 * Wraps com.github.mustachejava.Mustache to
 * encase and control mutability.
 * Mutability is bad, mmkay.
 */
class Mustache(
    private val mustache: JavaMustache
) {
  def execute(writer: Writer, scope:Any) = {
    mustache.execute(writer, scope)
  }

  def execute(writer: Writer, scopes:Seq[Any]) = {
    mustache.execute(writer, scopes)
  }
}
