organization := "com.kashoo"
name := """ws-limited"""
version := "5.0.0"
description := "Play library offering simple rate limiting of WSClient requests based on application configuration"
licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/Kashoo/ws-limited"))

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  guice,
  ws,
  "org.mockito" % "mockito-core" % "1.9.5" % Test,
  "de.leanovate.play-mockws" %% "play-mockws" % "2.8.1" % Test,
  specs2 % Test
)

coverageExcludedPackages := "<empty>;Reverse.*"

resolvers ++= Seq("scalaz-bintray" at "https://dl.bintray.com/scalaz/releases")

publishMavenStyle := true
Test / publishArtifact := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := <scm>
  <url>git@github.com:Kashoo/ws-limited.git</url>
  <connection>scm:git:git@github.com:Kashoo/ws-limited.git</connection>
</scm>
<developers>
  <developer>
    <id>dkichler</id>
    <name>Dave Kichler</name>
    <url>https://github.com/dkichler</url>
  </developer>
</developers>
