import sbtappengine.Plugin.{AppengineKeys => gae}

scalaVersion := "2.9.1"

seq(appengineSettings: _*)

libraryDependencies ++= Seq (
  "com.github.philcali" %% "lmxml-html" % "0.1.1-SNAPSHOT",
  "com.github.philcali" %% "lmxml-template" % "0.1.1-SNAPSHOT",
  "com.github.philcali" %% "lmxml-json" % "0.1.1-SNAPSHOT",
  "com.github.philcali" %% "lmxml-cache" % "0.1.1-SNAPSHOT",
  "com.github.philcali" %% "scalendar" % "0.1.1",
  "net.databinder" %% "unfiltered-filter" % "0.5.3",
  "net.databinder" %% "unfiltered-jetty" % "0.5.3",
  "net.databinder" %% "unfiltered-json" % "0.5.3",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"
)

unmanagedJars in Compile <++= gae.libPath in Compile map { libPath =>
  (libPath / "user" * "*.jar").classpath
}
