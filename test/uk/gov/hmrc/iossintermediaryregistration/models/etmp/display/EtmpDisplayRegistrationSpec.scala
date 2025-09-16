package uk.gov.hmrc.iossintermediaryregistration.models.etmp.display

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.*

class EtmpDisplayRegistrationSpec extends BaseSpec {

  private val etmpDisplayRegistration: EtmpDisplayRegistration = arbitraryEtmpDisplayRegistration.arbitrary.sample.value

  "EtmpDisplayRegistration" - {

    "must serialise to EtmpDisplayRegistration" in {

      val json = Json.obj(
        "customerIdentification" -> etmpDisplayRegistration.customerIdentification,
        "tradingNames" -> etmpDisplayRegistration.tradingNames,
        "clientDetails" -> etmpDisplayRegistration.clientDetails,
        "intermediaryDetails" -> etmpDisplayRegistration.intermediaryDetails,
        "otherAddress" -> etmpDisplayRegistration.otherAddress,
        "schemeDetails" -> Json.obj(
          "commencementDate" -> etmpDisplayRegistration.schemeDetails.commencementDate,
          "euRegistrationDetails" -> etmpDisplayRegistration.schemeDetails.euRegistrationDetails,
          "contactDetails" -> Json.obj(
            "contactNameOrBusinessAddress" -> etmpDisplayRegistration.schemeDetails.contactName,
            "businessTelephoneNumber" -> etmpDisplayRegistration.schemeDetails.businessTelephoneNumber,
            "businessEmailAddress" -> etmpDisplayRegistration.schemeDetails.businessEmailId,
            "unusableStatus" -> etmpDisplayRegistration.schemeDetails.unusableStatus
          ),
          "businessTelephoneNumber" -> etmpDisplayRegistration.schemeDetails.businessTelephoneNumber,
          "businessEmailId" -> etmpDisplayRegistration.schemeDetails.businessEmailId,
          "unusableStatus" -> etmpDisplayRegistration.schemeDetails.unusableStatus,
          "nonCompliantReturns" -> etmpDisplayRegistration.schemeDetails.nonCompliantReturns,
          "nonCompliantPayments" -> etmpDisplayRegistration.schemeDetails.nonCompliantPayments
        ),
        "exclusions" -> etmpDisplayRegistration.exclusions,
        "bankDetails" -> etmpDisplayRegistration.bankDetails,
        "adminUse" -> etmpDisplayRegistration.adminUse
      )

      val expectedResult = EtmpDisplayRegistration(
        customerIdentification = etmpDisplayRegistration.customerIdentification,
        tradingNames = etmpDisplayRegistration.tradingNames,
        clientDetails = etmpDisplayRegistration.clientDetails,
        intermediaryDetails = etmpDisplayRegistration.intermediaryDetails,
        otherAddress = etmpDisplayRegistration.otherAddress,
        schemeDetails = etmpDisplayRegistration.schemeDetails,
        exclusions = etmpDisplayRegistration.exclusions,
        bankDetails = etmpDisplayRegistration.bankDetails,
        adminUse = etmpDisplayRegistration.adminUse
      )

      json.validate[EtmpDisplayRegistration] `mustBe` JsSuccess(expectedResult)
    }

    "must deserialise from EtmpDisplayRegistration" in {

      val fromEtmpDisplayRegistration = EtmpDisplayRegistration(
        customerIdentification = etmpDisplayRegistration.customerIdentification,
        tradingNames = etmpDisplayRegistration.tradingNames,
        clientDetails = etmpDisplayRegistration.clientDetails,
        intermediaryDetails = etmpDisplayRegistration.intermediaryDetails,
        otherAddress = etmpDisplayRegistration.otherAddress,
        schemeDetails = etmpDisplayRegistration.schemeDetails,
        exclusions = etmpDisplayRegistration.exclusions,
        bankDetails = etmpDisplayRegistration.bankDetails,
        adminUse = etmpDisplayRegistration.adminUse
      )

      val expectedResult = Json.obj(
        "customerIdentification" -> etmpDisplayRegistration.customerIdentification,
        "tradingNames" -> etmpDisplayRegistration.tradingNames,
        "clientDetails" -> etmpDisplayRegistration.clientDetails,
        "intermediaryDetails" -> etmpDisplayRegistration.intermediaryDetails,
        "otherAddress" -> etmpDisplayRegistration.otherAddress,
        "schemeDetails" -> etmpDisplayRegistration.schemeDetails,
        "exclusions" -> etmpDisplayRegistration.exclusions,
        "bankDetails" -> etmpDisplayRegistration.bankDetails,
        "adminUse" -> etmpDisplayRegistration.adminUse
      )

      Json.toJson(fromEtmpDisplayRegistration) `mustBe` expectedResult
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpDisplayRegistration] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "tradingNames" -> 123456
      )

      json.validate[EtmpDisplayRegistration] `mustBe` a[JsError]
    }
  }
}
