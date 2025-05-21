package uk.gov.hmrc.iossintermediaryregistration.models.core

import uk.gov.hmrc.iossintermediaryregistration.models.{Enumerable, WithName}

sealed trait MatchType

object MatchType extends Enumerable.Implicits {

  case object TraderIdActiveNETP extends WithName("001") with MatchType

  case object TraderIdQuarantinedNETP extends WithName("002") with MatchType

  case object OtherMSNETPActiveNETP extends WithName("003") with MatchType

  case object OtherMSNETPQuarantinedNETP extends WithName("004") with MatchType

  case object FixedEstablishmentActiveNETP extends WithName("005") with MatchType

  case object FixedEstablishmentQuarantinedNETP extends WithName("006") with MatchType

  case object TransferringMSID extends WithName("007") with MatchType

  case object PreviousRegistrationFound extends WithName("008") with MatchType

  val values: Seq[MatchType] = Seq(
    TraderIdActiveNETP,
    TraderIdQuarantinedNETP,
    OtherMSNETPActiveNETP,
    OtherMSNETPQuarantinedNETP,
    FixedEstablishmentActiveNETP,
    FixedEstablishmentQuarantinedNETP,
    TransferringMSID,
    PreviousRegistrationFound
  )

  implicit val enumerable: Enumerable[MatchType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
