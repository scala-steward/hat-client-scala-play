import Dependencies._

libraryDependencies ++= Seq(
  Library.Play.ws,
  Library.Play.cache,
  Library.Play.test,
  Library.Play.specs2,
  Library.Specs2.matcherExtra,
  Library.Specs2.mock,
  Library.Specs2.core
)
