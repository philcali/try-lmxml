package lmxml
package web

import template.FileTemplates
import cache.FileHashes
import shortcuts.html.HtmlShortcuts

import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._

import javax.servlet.http.HttpServletRequest

import scala.io.Source.{fromFile => open}

import xml.PrettyPrinter

object LmxmlText extends 
  Params.Extract("lmxml-input", Params.first ~> Params.nonempty)

object XmlFormat extends Function1[xml.NodeSeq, String] {
  val printer = new PrettyPrinter(150, 2)
  def apply(nodes: xml.NodeSeq) = printer.formatNodes(nodes)
}

object Lmxml extends LmxmlFactory with Conversion {
  def createParser(step: Int) = new PlainLmxmlParser(step) with HtmlShortcuts
}

class LmxmlPlan extends Plan with LmxmlFactory with FileLoading {
  lazy val base = config.getServletContext.getRealPath(".")

  def createParser(step: Int) = {
    new PlainLmxmlParser(step) with HtmlShortcuts with FileTemplates {
      val working = new java.io.File(base)
    }
  }

  def intent = {
    case req @ GET(Path("/")) =>
      val index = new java.io.File(base, "index.lmxml")
      val converted = this.fromFile(index)(XmlConvert)

      ContentType("text/html") ~>
      ResponseString(converted.toString)
    case POST(Path("/") & Params(LmxmlText(text))) =>
      val process = XmlConvert andThen XmlFormat
      val resp = Lmxml(text).safeParseNodes(text).fold(_.toString, process)
      ContentType("text/plain") ~> ResponseString(resp)
  }
}
