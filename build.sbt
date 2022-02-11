name := "akka-essentials"

version := "0.1"

scalaVersion := "2.12.8"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.1"
lazy val protobufVersion = "3.6.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion, 
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5",
  "io.monix" %% "monix" % "3.3.0",
  "com.typesafe.akka" %% "akka-remote" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
)