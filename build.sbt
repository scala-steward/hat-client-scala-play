import Dependencies._

libraryDependencies ++= Seq(
  Library.Play.ws,
  Library.Play.test,
  Library.Play.specs2,
  Library.Play.json,
  Library.Play.jsonJoda,
  Library.Specs2.matcherExtra,
  Library.Specs2.mock,
  Library.Specs2.core,
  Library.Utils.jodaTime
)

dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-stream" % "2.5.4",
  "com.google.guava" % "guava" % "22.0",
  "com.google.code.findbugs" % "jsr305" % "3.0.2"
)

publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value("HAT Library Artifacts " + prefix, s3("library-artifacts-" + prefix + ".hubofallthings.com")) withMavenPatterns)
}
