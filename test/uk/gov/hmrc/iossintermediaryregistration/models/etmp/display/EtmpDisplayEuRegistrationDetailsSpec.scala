package uk.gov.hmrc.iossintermediaryregistration.models.etmp.display

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpDisplayEuRegistrationDetailsSpec extends BaseSpec {

  private val etmpDisplayEuRegistrationDetails: EtmpDisplayEuRegistrationDetails =
    arbitraryEtmpDisplayEuRegistrationDetails.arbitrary.sample.value

  "EtmpDisplayEuRegistrationDetailsSpec" - {

    "must deserialise/serialise from and to EtmpDisplayEuRegistrationDetails" - {

      "when all optional values are present" in {

        val json = Json.obj(
          "issuedBy" -> etmpDisplayEuRegistrationDetails.issuedBy,
          "vatNumber" -> etmpDisplayEuRegistrationDetails.vatNumber,
          "fixedEstablishmentTradingName" -> etmpDisplayEuRegistrationDetails.fixedEstablishmentTradingName,
          "fixedEstablishmentAddressLine1" -> etmpDisplayEuRegistrationDetails.fixedEstablishmentAddressLine1,
          "fixedEstablishmentAddressLine2" -> etmpDisplayEuRegistrationDetails.fixedEstablishmentAddressLine2,
          "townOrCity" -> etmpDisplayEuRegistrationDetails.townOrCity,
          "regionOrState" -> etmpDisplayEuRegistrationDetails.regionOrState,
          "postcode" -> etmpDisplayEuRegistrationDetails.postcode
        )

        val expectedResult = EtmpDisplayEuRegistrationDetails(
          issuedBy = etmpDisplayEuRegistrationDetails.issuedBy,
          vatNumber = etmpDisplayEuRegistrationDetails.vatNumber,
          taxIdentificationNumber = None,
          fixedEstablishmentTradingName = etmpDisplayEuRegistrationDetails.fixedEstablishmentTradingName,
          fixedEstablishmentAddressLine1 = etmpDisplayEuRegistrationDetails.fixedEstablishmentAddressLine1,
          fixedEstablishmentAddressLine2 = etmpDisplayEuRegistrationDetails.fixedEstablishmentAddressLine2,
          townOrCity = etmpDisplayEuRegistrationDetails.townOrCity,
          regionOrState = etmpDisplayEuRegistrationDetails.regionOrState,
          postcode = etmpDisplayEuRegistrationDetails.postcode
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpDisplayEuRegistrationDetails] `mustBe` JsSuccess(expectedResult)
      }

      "when all optional values are absent" in {

        val json = Json.obj(
          "issuedBy" -> etmpDisplayEuRegistrationDetails.issuedBy,
          "taxIdentificationNumber" -> etmpDisplayEuRegistrationDetails.taxIdentificationNumber,
          "fixedEstablishmentTradingName" -> etmpDisplayEuRegistrationDetails.fixedEstablishmentTradingName,
          "fixedEstablishmentAddressLine1" -> etmpDisplayEuRegistrationDetails.fixedEstablishmentAddressLine1,
          "townOrCity" -> etmpDisplayEuRegistrationDetails.townOrCity
        )

        val expectedResult = EtmpDisplayEuRegistrationDetails(
          issuedBy = etmpDisplayEuRegistrationDetails.issuedBy,
          vatNumber = None,
          taxIdentificationNumber = etmpDisplayEuRegistrationDetails.taxIdentificationNumber,
          fixedEstablishmentTradingName = etmpDisplayEuRegistrationDetails.fixedEstablishmentTradingName,
          fixedEstablishmentAddressLine1 = etmpDisplayEuRegistrationDetails.fixedEstablishmentAddressLine1,
          fixedEstablishmentAddressLine2 = None,
          townOrCity = etmpDisplayEuRegistrationDetails.townOrCity,
          regionOrState = None,
          postcode = None
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpDisplayEuRegistrationDetails] `mustBe` JsSuccess(expectedResult)
      }
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpDisplayEuRegistrationDetails] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "vatNumber" -> 123456
      )

      json.validate[EtmpDisplayEuRegistrationDetails] `mustBe` a[JsError]
    }
  }
}
