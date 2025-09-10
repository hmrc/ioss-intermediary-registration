package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpBankDetailsSpec extends BaseSpec {

  private val etmpBankDetails: EtmpBankDetails = arbitraryEtmpBankDetails.arbitrary.sample.value

  "EtmpBankDetailsSpec" - {

    "must deserialise/serialise from and to EtmpBankDetails" - {

      "when all optional values are present" in {

        val json = Json.obj(
          "accountName" -> etmpBankDetails.accountName,
          "bic" -> etmpBankDetails.bic,
          "iban" -> etmpBankDetails.iban
        )

        val expectedResult = EtmpBankDetails(
          accountName = etmpBankDetails.accountName,
          bic = etmpBankDetails.bic,
          iban = etmpBankDetails.iban
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpBankDetails] `mustBe` JsSuccess(expectedResult)
      }

      "when all optional values are absent" in {

        val json = Json.obj(
          "accountName" -> etmpBankDetails.accountName,
          "iban" -> etmpBankDetails.iban
        )

        val expectedResult = EtmpBankDetails(
          accountName = etmpBankDetails.accountName,
          bic = None,
          iban = etmpBankDetails.iban
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpBankDetails] `mustBe` JsSuccess(expectedResult)
      }
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpBankDetails] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "accountName" -> 123456
      )

      json.validate[EtmpBankDetails] `mustBe` a[JsError]
    }
  }
}
