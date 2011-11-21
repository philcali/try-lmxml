package lmxml
package web

import unfiltered.request._
import unfiltered.response._

import scala.io.Source.{fromFile => open}

import javax.servlet.http.HttpServletRequest

import xml.PrettyPrinter

case class Template(req: HttpRequest[HttpServletRequest]) {
  def apply(name: String) = {
    req.underlying.getRealPath(name + ".lmxml")
  }
}

object LmxmlText extends 
  Params.Extract("lmxml-input", Params.first ~> Params.nonempty)

object XmlFormat extends PrettyPrinter(150, 2) with Function1[xml.NodeSeq, String] {
  def apply(nodes: xml.NodeSeq) = formatNodes(nodes)
}

/** unfiltered plan */
class LmxmlPlan extends unfiltered.filter.Planify({
  case req @ GET(Path("/")) =>
    val converted = Lmxml.fromFile(Template(req)("index"))(XmlConvert)

    ContentType("text/html") ~>
    ResponseString(converted.toString)
  case POST(Path("/") & Params(LmxmlText(text))) =>
    val process = XmlConvert andThen XmlFormat

    val resp = Lmxml(text).safeParseNodes(text).fold(_.toString, process)

    ContentType("text/plain") ~> ResponseString(resp)
});
