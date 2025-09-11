package uk.gov.hmrc.iossintermediaryregistration.models.etmp.display

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.EtmpPreviousEuRegistrationDetails

class EtmpDisplaySchemeDetailsSpec extends BaseSpec {

  private val etmpDisplaySchemeDetails: EtmpDisplaySchemeDetails = arbitraryEtmpDisplaySchemeDetails.arbitrary.sample.value

  "EtmpDisplaySchemeDetails" - {

    "must deserialise/serialise from and to EtmpDisplaySchemeDetails" - {

      "when all optional values are present" in {

        val json = Json.obj(
          "commencementDate" -> etmpDisplaySchemeDetails.commencementDate,
          "euRegistrationDetails" -> etmpDisplaySchemeDetails.euRegistrationDetails,
          "previousEURegistrationDetails" -> etmpDisplaySchemeDetails.previousEURegistrationDetails,
          "contactName" -> etmpDisplaySchemeDetails.contactName,
          "businessTelephoneNumber" -> etmpDisplaySchemeDetails.businessTelephoneNumber,
          "businessEmailId" -> etmpDisplaySchemeDetails.businessEmailId,
          "unusableStatus" -> etmpDisplaySchemeDetails.unusableStatus,
          "nonCompliantReturns" -> etmpDisplaySchemeDetails.nonCompliantReturns,
          "nonCompliantPayments" -> etmpDisplaySchemeDetails.nonCompliantPayments
        )

        val expectedResult = EtmpDisplaySchemeDetails(
          commencementDate = etmpDisplaySchemeDetails.commencementDate,
          euRegistrationDetails = etmpDisplaySchemeDetails.euRegistrationDetails,
          previousEURegistrationDetails = etmpDisplaySchemeDetails.previousEURegistrationDetails,
          contactName = etmpDisplaySchemeDetails.contactName,
          businessTelephoneNumber = etmpDisplaySchemeDetails.businessTelephoneNumber,
          businessEmailId = etmpDisplaySchemeDetails.businessEmailId,
          unusableStatus = etmpDisplaySchemeDetails.unusableStatus,
          nonCompliantReturns = etmpDisplaySchemeDetails.nonCompliantReturns,
          nonCompliantPayments = etmpDisplaySchemeDetails.nonCompliantPayments
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpDisplaySchemeDetails] `mustBe` JsSuccess(expectedResult)
      }

      "when all optional values are absent" in {

        val json = Json.obj(
          "commencementDate" -> etmpDisplaySchemeDetails.commencementDate,
          "euRegistrationDetails" -> etmpDisplaySchemeDetails.euRegistrationDetails,
          "previousEURegistrationDetails" -> etmpDisplaySchemeDetails.previousEURegistrationDetails,
          "contactName" -> etmpDisplaySchemeDetails.contactName,
          "businessTelephoneNumber" -> etmpDisplaySchemeDetails.businessTelephoneNumber,
          "businessEmailId" -> etmpDisplaySchemeDetails.businessEmailId,
          "unusableStatus" -> etmpDisplaySchemeDetails.unusableStatus
        )

        val expectedResult = EtmpDisplaySchemeDetails(
          commencementDate = etmpDisplaySchemeDetails.commencementDate,
          euRegistrationDetails = etmpDisplaySchemeDetails.euRegistrationDetails,
          previousEURegistrationDetails = etmpDisplaySchemeDetails.previousEURegistrationDetails,
          contactName = etmpDisplaySchemeDetails.contactName,
          businessTelephoneNumber = etmpDisplaySchemeDetails.businessTelephoneNumber,
          businessEmailId = etmpDisplaySchemeDetails.businessEmailId,
          unusableStatus = etmpDisplaySchemeDetails.unusableStatus,
          nonCompliantReturns = None,
          nonCompliantPayments = None
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpDisplaySchemeDetails] `mustBe` JsSuccess(expectedResult)
      }
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpDisplaySchemeDetails] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "contactName" -> 123456
      )

      json.validate[EtmpDisplaySchemeDetails] `mustBe` a[JsError]
    }
  }
}
