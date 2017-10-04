package uk.gov.bis.userinfoApiMock

import enumeratum.EnumFormats
import play.api.libs.json.{Format, Json, OFormat}

package object models {
  implicit val ggFormat            : OFormat[GovernmentGateway] = Json.format
  implicit val addressFormat       : OFormat[Address]           = Json.format
  implicit val enrolmentStateFormat: Format[EnrolmentState]     = EnumFormats.formats(EnrolmentState)
  implicit val identifierFormat    : OFormat[Identifier]        = Json.format
  implicit val enrolmentFormat     : OFormat[Enrolment]         = Json.format
  implicit val enrolmentsFormat    : OFormat[Enrolments]        = Json.format
  implicit val userinfoFormat      : OFormat[UserInfo]          = Json.format
}
