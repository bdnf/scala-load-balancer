name := "akka-proxy"

version := "0.1"

scalaVersion := "2.12.8"

val akkaHttpVersion = "10.1.7"
val scalaTestVersion = "3.0.5"
val akkaVersion = "2.5.20"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "io.circe"          %% "circe-generic" % "0.10.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
  // testing
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,

)