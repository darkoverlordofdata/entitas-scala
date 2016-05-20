scalaJSUseRhino in Global := false

enablePlugins(ScalaJSPlugin)

name := "Entitas Demo"

scalaVersion := "2.11.7" // or any other Scala version >= 2.10.2

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"

skip in packageJSDependencies := false
//jsDependencies +=
//  "org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js"
//
//resolvers += "amateras-repo" at "http://amateras.sourceforge.jp/mvn/"
//
////libraryDependencies += "com.scalawarrior" %%% "scalajs-createjs" % "0.0.2"
