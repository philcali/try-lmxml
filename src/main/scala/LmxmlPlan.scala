package lmxml
package web

import unfiltered.request._
import unfiltered.response._

import scala.io.Source.{fromFile => open}

import javax.servlet.http.HttpServletRequest

import xml.PrettyPrinter

case class Template(req: HttpRequest[HttpServletRequest]) {
  def apply(name: String) = {
    val base = req.underlying.getRealPath(name + ".lmxml")
    open(base).getLines.mkString("\n")
  }
}

object LmxmlText extends 
  Params.Extract("lmxml-input", Params.first ~> Params.nonempty)

object Printer extends PrettyPrinter(150, 2)

/** unfiltered plan */
class LmxmlPlan extends unfiltered.filter.Planify({
  case req @ GET(Path("/index.lmxml")) =>
    val template = Template(req)
    ContentType("text/plain") ~> ResponseString(template("index"))
  case req @ GET(Path("/")) =>
    val template = Template(req)
    val converted = Lmxml.convert(template("index"))(XmlConverter)
    ContentType("text/html") ~>
    ResponseString("<!DOCTYPE html>\n" + converted)
  case POST(Path("/") & Params(LmxmlText(text))) =>
    val resp = Lmxml(text).safeParseNodes(text).fold(_.toString, { parsed =>
      Printer.formatNodes(XmlConverter.convert(parsed))
    })
    ContentType("text/plain") ~> ResponseString(resp)
});
