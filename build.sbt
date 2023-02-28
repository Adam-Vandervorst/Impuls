ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "3.2.1"

lazy val root = crossProject(JSPlatform, JVMPlatform, NativePlatform).withoutSuffixFor(JVMPlatform)
  .in(file("."))
  .settings(
    name := "Impuls",
    idePackagePrefix := Some("be.adamv.impuls"),
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
    libraryDependencies += "be.adamv" %%% "momentum" % "0.3.0",
    scalaJSUseMainModuleInitializer := true,
    publishTo := Some(Resolver.file("local-ivy", file("~")))
  )
