scalaJSUseRhino in Global := false

enablePlugins(ScalaJSPlugin)

name := "Entitas"
organization :=  "com.darkoverlordofdata"
version := "0.0.0-SNAPSHOT"

scalaVersion := "2.11.7" // or any other Scala version >= 2.10.2

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://darkoverlordofdata.com/entitas-scala</url>
    <licenses>
      <license>
        <name>MIT-style</name>
        <url>https://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:darkoverlordofdata/entitas-scala.git</url>
      <connection>scm:git:git@github.com:darkoverlordofdata/entitas-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>darkoverlordofdata</id>
        <name>Bruce Davidson</name>
        <url>https://darkoverlordofdata.com</url>
      </developer>
    </developers>)

