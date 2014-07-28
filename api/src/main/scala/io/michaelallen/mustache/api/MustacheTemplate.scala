package io.michaelallen.mustache.api

trait MustacheTemplate extends MustacheRenderer {
  val mustache: Mustache

  def render(): String = {
    render(mustache, this)
  }
}
