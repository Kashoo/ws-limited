name := """ws-limited-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  ws,
  "com.kashoo" %% "ws-limited" % "0.1.0" % Compile,
  "org.mock-server" % "mockserver-netty" % "3.10.4" % Compile
)
