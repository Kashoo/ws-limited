
organization := "com.kashoo"

name := """ws-limited"""

version := "0.1.0"

description := "Play library offering simple rate limiting of WSClient requests based on application configuration"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  ws,
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

coverageExcludedPackages := "<empty>;Reverse.*"

resolvers ++= Seq("Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
                  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases")
