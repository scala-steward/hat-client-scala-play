package org.hatdex.hat.api.models

import java.util.UUID

import org.joda.time.{ DateTime, Duration, LocalDateTime }
import play.api.libs.json._

case class EndpointData(
    endpoint: String,
    recordId: Option[UUID],
    sourceTimestamp: Option[DateTime],
    sourceUniqueId: Option[String],
    data: JsValue,
    links: Option[Seq[EndpointData]])

object FilterOperator {
  trait Operator {
    val operator: String
  }

  case class In(value: JsValue) extends Operator {
    val operator = "in"
  }
  case class Contains(value: JsValue) extends Operator {
    val operator = "contains"
  }
  case class Between(lower: JsValue, upper: JsValue) extends Operator {
    val operator = "between"
  }
  case class Find(search: JsValue) extends Operator {
    val operator = "matches"
  }
}

object FieldTransformation {
  sealed trait Transformation
  case class Identity() extends Transformation
  case class DateTimeExtract(part: String) extends Transformation
  case class TimestampExtract(part: String) extends Transformation
  case class Searchable() extends Transformation
}

case class EndpointQueryFilter(
    field: String,
    transformation: Option[FieldTransformation.Transformation],
    operator: FilterOperator.Operator) {
  def originalField: List[String] = {
    field.split('.').toList
  }
}

case class EndpointQuery(
    endpoint: String,
    mapping: Option[JsValue],
    filters: Option[Seq[EndpointQueryFilter]],
    links: Option[Seq[EndpointQuery]]) {
  def originalField(field: String): Option[List[String]] = {
    mapping.flatMap { m =>
      (m \ field)
        .toOption
        .map(_.as[String].split('.').toList)
    } orElse {
      Some(field.split('.').toList)
    }
  }
}

case class PropertyQuery(
    endpoints: List[EndpointQuery],
    orderBy: Option[String],
    ordering: Option[String],
    limit: Option[Int])

case class EndpointDataBundle(
    name: String,
    bundle: Map[String, PropertyQuery]) {

  // Get a flat list of endpoint queries, primarily for detecting presence of data in a bundle
  lazy val flatEndpointQueries: Seq[EndpointQuery] = bundle.values
    .flatMap(_.endpoints.flatMap(endpointQueries))
    .toSeq

  private def endpointQueries(endpointQuery: EndpointQuery): Seq[EndpointQuery] = {
    endpointQuery.links
      .map(_.flatMap(endpointQueries))
      .getOrElse(Seq()) :+ endpointQuery
  }
}

@Deprecated
case class RichDataDebit(
    dataDebitKey: String,
    dateCreated: LocalDateTime,
    client: User,
    bundles: Seq[DebitBundle]) {

  lazy val currentBundle: Option[DebitBundle] = bundles.sortBy(_.dateCreated).headOption

  private implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isAfter _)
  lazy val activeBundle: Option[DebitBundle] =
    bundles.filter { b =>
      b.enabled && b.startDate.isBefore(LocalDateTime.now()) &&
        (b.endDate.isAfter(LocalDateTime.now()) || b.rolling)
    }
      .sortBy(_.dateCreated)
      .headOption

  lazy val lastUpdated: LocalDateTime =
    bundles.sortBy(_.dateCreated)
      .headOption
      .map(_.dateCreated)
      .getOrElse(LocalDateTime.now())

}

@Deprecated
case class RichDataDebitData(
    conditions: Option[Map[String, Boolean]],
    bundle: Map[String, Seq[EndpointData]])

@Deprecated
case class DebitBundle(
    dateCreated: LocalDateTime,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    rolling: Boolean,
    enabled: Boolean,
    conditions: Option[EndpointDataBundle],
    bundle: EndpointDataBundle)

@Deprecated
case class DataDebitRequest(
    bundle: EndpointDataBundle,
    conditions: Option[EndpointDataBundle],
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    rolling: Boolean)

// Structure replacing RichDataDebit to contain more info

case class DataDebitPermissions(
    dateCreated: LocalDateTime,
    purpose: String, // The purpose of processing data for regulatory compliance
    start: DateTime, // Start of the data debit
    period: Duration, // How long does it run for - a day/week/month/etc?
    cancelAtPeriodEnd: Boolean, // should it be cancelled at the end of the current period?
    canceledAt: Option[DateTime], // when was it cancelled - set at the time of cancellation, if set and cancelAtPeriodEnd=false, cancel immediately
    termsUrl: String, // URL linking to terms and conditions of this data debit
    conditions: Option[EndpointDataBundle],
    bundle: EndpointDataBundle,
    accepted: Boolean) {
  lazy val active: Boolean = {
    val now = DateTime.now()
    if (start.isAfter(now)) {
      false
    }
    else {
      accepted && end.forall(_.isAfter(now)) // if end date is set, only active if it is after now; no end date - active
    }
  }

  lazy val end: Option[DateTime] = {
    (canceledAt, cancelAtPeriodEnd) match {
      case (Some(canceled), false) ⇒ Some(canceled) // has been cancelled with immediate effect
      case (Some(canceled), true)  ⇒ Some(start.plus(math.ceil((canceled.getMillis - start.getMillis).toDouble / period.getMillis.toDouble).toLong * period.getMillis)) // finish at the end of period
      case (None, false)           ⇒ None // rolling indefinitely
      case (None, true)            ⇒ Some(start.plus(period)) // the validity period finished
    }
  }
}

case class DataDebit(
    dataDebitKey: String,
    dateCreated: LocalDateTime,
    permissions: Seq[DataDebitPermissions],
    requestClientName: String,
    requestClientUrl: String,
    requestClientLogoUrl: String,
    requestApplicationId: Option[String],
    requestDescription: Option[String] // High level description (may be empty) of what the Data Debit is about
) {

  private implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isAfter _)

  lazy val currentPermissions: Option[DataDebitPermissions] = permissions.sortBy(_.dateCreated).headOption

  lazy val activePermissions: Option[DataDebitPermissions] =
    permissions.filter(_.active)
      .sortBy(_.dateCreated)
      .headOption

  lazy val lastUpdated: LocalDateTime =
    if (permissions.nonEmpty) {
      permissions.map(_.dateCreated).max
    }
    else {
      LocalDateTime.now()
    }

  lazy val accepted: Boolean = permissions.exists(_.accepted)
  lazy val active: Boolean = activePermissions.exists(_.active)
  lazy val start: Option[DateTime] = activePermissions.orElse(currentPermissions).map(p ⇒ p.start)
  lazy val end: Option[DateTime] = activePermissions.orElse(currentPermissions).flatMap(p ⇒ p.end)
}

case class DataDebitSetupRequest(
    dataDebitKey: String, // final
    purpose: String, // The purpose of processing data for regulatory compliance
    start: DateTime, // Start of the data debit
    period: Duration, // How long does it run for - a day/week/month/etc?
    cancelAtPeriodEnd: Boolean, // should it be cancelled at the end of the current period?
    requestClientName: String, // Final
    requestClientUrl: String, // Final
    requestClientLogoUrl: String, // Final
    requestClientCallbackUrl: Option[String], // the url to notify when data is received on the defined endpoints
    requestApplicationId: Option[String], // Final
    requestDescription: Option[String], // Final
    termsUrl: String, // URL linking to terms and conditions of this data debit
    conditions: Option[EndpointDataBundle],
    bundle: EndpointDataBundle)

case class DataDebitData(
    conditions: Option[Map[String, Boolean]],
    bundle: Map[String, Seq[EndpointData]])
