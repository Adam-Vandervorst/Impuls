ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "Impuls",
    idePackagePrefix := Some("be.adamv.impuls"),
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "be.adamv" %% "momentum" % "0.2.2",
  )
