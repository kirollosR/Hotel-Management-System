ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val AkkaVersion = "2.8.0"

lazy val root = (project in file("."))
  .settings(
    name := "Hotel-Management-System",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.slick" %% "slick" % "3.3.3",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
      "mysql" % "mysql-connector-java" % "8.0.23"
    )
  )
