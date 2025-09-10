package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpPreviousEuRegistrationDetailsSpec extends BaseSpec {

  private val etmpEuPreviousRegistrationDetails: EtmpPreviousEuRegistrationDetails =
    arbitraryEtmpPreviousEuRegistrationDetails.arbitrary.sample.value

  "EtmpPreviousEuRegistrationDetailsSpec" - {

    "must deserialise/serialise from and to EtmpPreviousEuRegistrationDetails" - {

      "when all optional values are present" in {

        val json = Json.obj(
          "issuedBy" -> etmpEuPreviousRegistrationDetails.issuedBy,
          "registrationNumber" -> etmpEuPreviousRegistrationDetails.registrationNumber,
          "schemeType" -> etmpEuPreviousRegistrationDetails.schemeType,
          "intermediaryNumber" -> etmpEuPreviousRegistrationDetails.intermediaryNumber
        )

        val expectedResult = EtmpPreviousEuRegistrationDetails(
          issuedBy = etmpEuPreviousRegistrationDetails.issuedBy,
          registrationNumber = etmpEuPreviousRegistrationDetails.registrationNumber,
          schemeType = etmpEuPreviousRegistrationDetails.schemeType,
          intermediaryNumber = etmpEuPreviousRegistrationDetails.intermediaryNumber
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpPreviousEuRegistrationDetails] `mustBe` JsSuccess(expectedResult)
      }

      "when all optional values are absent" in {

        val json = Json.obj(
          "issuedBy" -> etmpEuPreviousRegistrationDetails.issuedBy,
          "registrationNumber" -> etmpEuPreviousRegistrationDetails.registrationNumber,
          "schemeType" -> etmpEuPreviousRegistrationDetails.schemeType
        )

        val expectedResult = EtmpPreviousEuRegistrationDetails(
          issuedBy = etmpEuPreviousRegistrationDetails.issuedBy,
          registrationNumber = etmpEuPreviousRegistrationDetails.registrationNumber,
          schemeType = etmpEuPreviousRegistrationDetails.schemeType,
          intermediaryNumber = None
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpPreviousEuRegistrationDetails] `mustBe` JsSuccess(expectedResult)
      }
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpPreviousEuRegistrationDetails] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "issuedBy" -> 123456
      )

      json.validate[EtmpPreviousEuRegistrationDetails] `mustBe` a[JsError]
    }
  }
}
