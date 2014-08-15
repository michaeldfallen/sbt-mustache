package controllers

import play.api._
import play.api.mvc._
import io.michaelallen.mustache.PlayImplicits

object Application extends Controller with PlayImplicits {

  def index = Action {
    Ok(
      views.Test(
        title = "Rendered by Mustache",
        content = "Mustache is working"
      )
    )
  }

}
