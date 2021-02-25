val scalaV = "2.13.5"
val zioV = "1.0.4-2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "zlmdb",
    version := "0.1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioV,
      "dev.zio" %% "zio-test"     % zioV % "test",
      "dev.zio" %% "zio-test-sbt" % zioV % "test",
      "org.lmdbjava" % "lmdbjava" % "0.8.1",
      "dev.zio" %% "zio-nio" % "1.0.0-RC10"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
