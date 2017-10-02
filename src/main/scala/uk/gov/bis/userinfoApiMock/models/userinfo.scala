package uk.gov.bis.userinfoApiMock.models

import enumeratum.EnumEntry.{Camelcase, CapitalWords}
import enumeratum._

/*
* These classes model the response structure of the MDTP /userinfo api call. Since they
* map names and values from that json structure some of the case class property names
* use snake_case rather than the scala convention of camelCase.
 */


/**
  * The api docs say the enrolment state values are limited to:
  * awaitingActivation
  * activated
  * Active
  * Activated
  * pending
  * givenToAgent
  *
  * so I have provided for both capitalised and camel case entries.
  */
sealed trait EnrolmentState extends EnumEntry with CapitalWords with Camelcase

object EnrolmentState extends Enum[EnrolmentState] with PlayEnum[EnrolmentState] {
  //noinspection TypeAnnotation
  val values = findValues

  case object AwaitingActivation extends EnrolmentState
  case object Activated extends EnrolmentState
  case object Active extends EnrolmentState
  case object Pending extends EnrolmentState
  case object GivenToAgent extends EnrolmentState
}

case class Enrolment(key: String, identifiers: Map[String, String], state: EnrolmentState)

case class GovernmentGateway(
  user_id: Option[String],
  roles: Option[Seq[String]],
  affinity_group: String,
  agent_code: Option[String],
  agent_friendly_name: Option[String],
  agent_id: Option[String],
  gateway_token: Option[String]
)

case class Address(
  formatted: Option[String],
  postal_code: Option[String],
  country: Option[String],
  country_code: Option[String]
)

case class UserInfo(
  given_name: Option[String],
  family_name: Option[String],
  middle_name: Option[String],
  email: Option[String],
  birthdate: Option[String],
  uk_gov_nino: Option[String],
  address: Option[Address],
  hmrc_enrolments: Option[Seq[Enrolment]],
  government_gateway: Option[GovernmentGateway]
)