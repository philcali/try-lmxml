scalaVersion := "2.8.1"

seq(appengineSettings: _*)

libraryDependencies ++= Seq (
  "com.github.philcali" %% "lmxml-core" % "0.1.0",
  "net.liftweb" %% "lift-json" % "2.3",
  "net.databinder" %% "unfiltered-filter" % "0.5.1",
  "net.databinder" %% "unfiltered-jetty" % "0.5.1",
  "net.databinder" %% "unfiltered-json" % "0.5.1",
  "net.databinder" %% "unfiltered-spec" % "0.5.1" % "test",
  "org.scala-tools.testing" %% "specs" %"1.6.6" % "test",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "jetty"
)
