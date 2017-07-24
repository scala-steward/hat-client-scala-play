import Dependencies._

libraryDependencies ++= Seq(
  Library.Play.ws,
  Library.Play.cache,
  Library.Play.test,
  Library.Play.specs2,
  Library.Specs2.matcherExtra,
  Library.Specs2.mock,
  Library.Specs2.core,
  Library.Utils.jodaTime
)

publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value("HAT Library Artifacts " + prefix, s3("library-artifacts-" + prefix + ".hubofallthings.com")) withMavenPatterns)
}
