ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.danielroy"

val ScalaVersion          = "2.13.8"
val ZIOVersion            = "2.0.0-RC2"
val ZIOConfigVersion      = "3.0.0-RC3"
val ZIOHttpVersion        = "2.0.0-RC4"
val ZIOJsonVersion        = "0.3.0-RC3"
val QuillVersion          = "3.17.0-RC2"

lazy val DatabaseDependencies = Seq(
  "io.getquill" %% "quill-zio" % QuillVersion,
  "io.getquill" %% "quill-jdbc-zio" % QuillVersion,
  "com.h2database" % "h2" % "2.1.212"
)

lazy val ZioHttpDependencies = Seq(
  "io.d11" %% "zhttp"      % ZIOHttpVersion,
  "io.d11" %% "zhttp-test" % ZIOHttpVersion % Test
)

lazy val commonSettings = Seq(
  organization := "com.danielroy",
  scalaVersion := ScalaVersion,
  testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  dockerRepository := Some("localhost:8080"),
  libraryDependencies ++= Seq(
    "dev.zio"                %% "zio"                 % ZIOVersion,
    "dev.zio"                %% "zio-test"            % ZIOVersion,
    "dev.zio"                %% "zio-test-sbt"        % ZIOVersion,
    "dev.zio"                %% "zio-config"          % ZIOConfigVersion,
    "dev.zio"                %% "zio-config-refined"  % ZIOConfigVersion,
    "dev.zio"                %% "zio-config-typesafe" % ZIOConfigVersion,
    "dev.zio"                %% "zio-config-magnolia" % ZIOConfigVersion,
    "eu.timepit"             %% "refined"             % "0.9.28"
  )
)

lazy val model = (project in file("model"))
  .settings(commonSettings: _*)
  .settings(
    name := "model",
    publishLocal in Docker := {},
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-json" % ZIOJsonVersion
    )
  )


lazy val api = (project in file("api"))
  .settings(commonSettings: _*)
  .settings(
    name := "api",
    mainClass in Compile := Some("com.danielroy.amazonreviews.api.Main"),
    libraryDependencies ++= ZioHttpDependencies ++ DatabaseDependencies,
    dockerExposedPorts := Seq(80)
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(model)

lazy val ingestion = (project in file("ingestion"))
  .settings(commonSettings: _*)
  .settings(
    name := "ingestion",
    mainClass in Compile := Some("com.danielroy.amazonreviews.ingestion.Main"),
    libraryDependencies ++= DatabaseDependencies,
    dockerExposedPorts := Seq(80)
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(model)


lazy val amazonreviews = (project in file("."))
  .aggregate(model, api, ingestion)
  .settings(commonSettings: _*)
  .settings(
    name := "amazonreviews",
    publish := {}
  )
