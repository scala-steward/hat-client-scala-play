package org.hatdex.hat.api.models.applications

import org.joda.time.DateTime

case class DataFeedItem(
    source: String,
    date: DateTime,
    types: Seq[String],
    title: Option[DataFeedItemTitle],
    content: Option[DataFeedItemContent],
    location: Option[DataFeedItemLocation]) {
  lazy val unix: Long = date.getMillis / 1000L // UNIX timestamp in seconds
}

case class DataFeedItemTitle(
    text: String,
    action: Option[String])

case class DataFeedItemContent(
    text: Option[String],
    media: Option[Seq[DataFeedItemMedia]])

case class DataFeedItemMedia(
    url: Option[String])

case class DataFeedItemLocation(
    geo: Option[LocationGeo],
    address: Option[LocationAddress],
    tags: Option[Seq[String]])

case class LocationGeo(
    longitude: Double,
    latitude: Double)

case class LocationAddress(
    country: Option[String],
    city: Option[String],
    name: Option[String],
    street: Option[String],
    zip: Option[String])
