package lmxml
package web

import template.FileTemplates
import cache.FileHashes
import shortcuts.html.HtmlShortcuts
import transforms.json.JSTransform
import markdown.MarkdownParsing

import converters.json.JsonConvert

import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._

import javax.servlet.http.HttpServletRequest

import scala.io.Source.{fromFile => open}

import xml.PrettyPrinter

import util.parsing.json.{
  JSONObject,
  JSONArray
}

object LmxmlText extends
  Params.Extract("lmxml-input", Params.first ~> Params.nonempty)

object JsonText extends
  Params.Extract("lmxml-json", Params.first ~> Params.nonempty)

object LmxmlConversion extends
  Params.Extract("lmxml-conversion", Params.first ~> Params.nonempty)

class LmxmlPlan extends Plan with LmxmlFactory with FileHashes {
  val storage = AppEngineCache

  lazy val base = config.getServletContext.getRealPath(".")

  def createParser(step: Int) = {
    new PlainLmxmlParser(step)
      with MarkdownParsing with HtmlShortcuts with FileTemplates {
      val working = new java.io.File(base)
    }
  }

  def intent = {
    case GET(Path("/")) =>
      val index = new java.io.File(base, "index.lmxml")
      val converted = this.fromFile(index)(XmlConvert)

      ContentType("text/html") ~>
      ResponseString(converted.toString)
    case POST(Path("/") &
         Params(LmxmlText(text)) &
         Params(JsonText(jsonStr)) &
         Params(LmxmlConversion(source))) =>
      val js = JSTransform().parse(jsonStr)

      val process = js andThen (source match {
        case "json" => JsonConvert andThen (_.toString())
        case _ => XmlConvert andThen XmlFormat(200, 2)
      })

      val resp = apply(text).safeParseNodes(text).fold(_.toString, process)
      ContentType("text/plain") ~> ResponseString(resp)
  }
}
