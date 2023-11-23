ThisBuild / version := "0.2.2c"
ThisBuild / scalaVersion := "3.4.0-RC1-bin-20231122-637ed88-NIGHTLY"

lazy val root = crossProject(JSPlatform, JVMPlatform, NativePlatform).withoutSuffixFor(JVMPlatform)
  .in(file("."))
  .settings(
    name := "Impuls",
    organization := "be.adamv",
    idePackagePrefix := Some("be.adamv.impuls"),
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
    libraryDependencies += "be.adamv" %%% "momentum" % "0.4.1",
    scalaJSUseMainModuleInitializer := true,
    publishTo := Some(Resolver.file("local-ivy", file("~")))
  )
