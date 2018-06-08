package org.hatdex.hat.api.json

import org.hatdex.hat.api.models.applications._
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait DataFeedItemJsonProtocol extends JodaWrites with JodaReads {
  protected implicit val locationGeoFormat: Format[LocationGeo] = Json.format[LocationGeo]
  protected implicit val locationAddressFormat: Format[LocationAddress] = Json.format[LocationAddress]
  protected implicit val locationFormat: Format[DataFeedItemLocation] = Json.format[DataFeedItemLocation]
  protected implicit val mediaFormat: Format[DataFeedItemMedia] = Json.format[DataFeedItemMedia]
  protected implicit val dataFeedNestedStructureItemFormat: Format[DataFeedNestedStructureItem] = Json.format[DataFeedNestedStructureItem]
  protected implicit val contentFormat: Format[DataFeedItemContent] = Json.format[DataFeedItemContent]
  protected implicit val titleFormat: Format[DataFeedItemTitle] = Json.format[DataFeedItemTitle]

  implicit val feedItemWrites: Writes[DataFeedItem] = (
    (JsPath \ "source").write[String] and
    (JsPath \ "date" \ "iso").write[DateTime] and
    (JsPath \ "date" \ "unix").write[Long] and
    (JsPath \ "types").write[Seq[String]] and
    (JsPath \ "title").writeNullable[DataFeedItemTitle] and
    (JsPath \ "content").writeNullable[DataFeedItemContent] and
    (JsPath \ "location").writeNullable[DataFeedItemLocation])(t =>
      (t.source, t.date, t.date.getMillis / 1000L, t.types, t.title, t.content, t.location))

  implicit val feedItemReads: Reads[DataFeedItem] = (
    (JsPath \ "source").read[String] and
    (JsPath \ "date" \ "iso").read[DateTime] and
    (JsPath \ "types").read[Seq[String]] and
    (JsPath \ "title").readNullable[DataFeedItemTitle] and
    (JsPath \ "content").readNullable[DataFeedItemContent] and
    (JsPath \ "location").readNullable[DataFeedItemLocation])(DataFeedItem.apply _)

  implicit val feedItemFormat: Format[DataFeedItem] = Format(feedItemReads, feedItemWrites)
}

object DataFeedItemJsonProtocol extends DataFeedItemJsonProtocol {

}
