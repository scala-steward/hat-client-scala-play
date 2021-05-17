import Dependencies._

libraryDependencies ++= Seq(
  Library.playWs,
  Library.playJson,
  Library.playJsonJoda,
  Library.jodaTime,
  Library.hatPlayModels,
  Library.playServer,
  Library.Specs2.matcherExtra % Test,
  Library.Specs2.mock         % Test,
  Library.Specs2.core         % Test,
  Library.playAkkaHttpServer  % Test
)
publishMavenStyle := true
publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(
    "Models" + prefix at "s3://library-artifacts-" + prefix + ".hubofallthings.com"
  )
}

inThisBuild(
  List(
    scalaVersion := "2.13.6",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := "2.13",
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
  )
)
