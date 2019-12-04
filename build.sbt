name := "sk.scala.kafka"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.19"
lazy val scalaTestVersion = "3.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  //"org.apache.kafka" %% "kafka" % "2.1.1",
  "com.typesafe.akka" %% "akka-stream-kafka" % "1.1.0"
)
