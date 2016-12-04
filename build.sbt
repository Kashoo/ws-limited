
organization := "com.kashoo"

name := """ws-limited"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "de.leanovate.play-mockws" %% "play-mockws" % "2.4.2" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
)

coverageExcludedPackages := "<empty>;Reverse.*"

resolvers ++= Seq("Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
                  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases")

