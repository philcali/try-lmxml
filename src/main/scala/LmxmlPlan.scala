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
  case req @ GET(Path("/")) =>
    val template = Template(req)
    val converted = Lmxml.convert(template("index"))(XmlConverter)
    ContentType("text/html") ~>
    ResponseString("<!DOCTYPE html>\n" + converted)
  case req @ POST(Path("/") & Params(LmxmlText(text))) =>
    val xml = Printer.formatNodes(Lmxml.convert(text)(XmlConverter))
    
    ContentType("text/plain") ~> ResponseString(xml)
});
