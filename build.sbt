name := "bank-pet"

version := "0.1"

scalaVersion := "2.13.4"

val AkkaVersion = "2.6.10"
val AkkaHttpVersion = "10.2.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
)