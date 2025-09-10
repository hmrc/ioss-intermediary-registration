package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpOtherAddressSpec extends BaseSpec {

  private val etmpOtherAddress: EtmpOtherAddress = arbitraryEtmpOtherAddress.arbitrary.sample.value

  "EtmpOtherAddressSpec" - {

    "must deserialise/serialise from and to EtmpOtherAddress" - {

      "when all optional values are present" in {

        val json = Json.obj(
          "issuedBy" -> etmpOtherAddress.issuedBy,
          "tradingName" -> etmpOtherAddress.tradingName,
          "addressLine1" -> etmpOtherAddress.addressLine1,
          "addressLine2" -> etmpOtherAddress.addressLine2,
          "townOrCity" -> etmpOtherAddress.townOrCity,
          "regionOrState" -> etmpOtherAddress.regionOrState,
          "postcode" -> etmpOtherAddress.postcode
        )

        val expectedResult: EtmpOtherAddress = EtmpOtherAddress(
          issuedBy = etmpOtherAddress.issuedBy,
          tradingName = etmpOtherAddress.tradingName,
          addressLine1 = etmpOtherAddress.addressLine1,
          addressLine2 = etmpOtherAddress.addressLine2,
          townOrCity = etmpOtherAddress.townOrCity,
          regionOrState = etmpOtherAddress.regionOrState,
          postcode = etmpOtherAddress.postcode
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpOtherAddress] `mustBe` JsSuccess(expectedResult)
      }

      "when all optional values are absent" in {

        val json = Json.obj(
          "issuedBy" -> etmpOtherAddress.issuedBy,
          "addressLine1" -> etmpOtherAddress.addressLine1,
          "townOrCity" -> etmpOtherAddress.townOrCity,
          "postcode" -> etmpOtherAddress.postcode
        )

        val expectedResult: EtmpOtherAddress = EtmpOtherAddress(
          issuedBy = etmpOtherAddress.issuedBy,
          tradingName = None,
          addressLine1 = etmpOtherAddress.addressLine1,
          addressLine2 = None,
          townOrCity = etmpOtherAddress.townOrCity,
          regionOrState = None,
          postcode = etmpOtherAddress.postcode
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpOtherAddress] `mustBe` JsSuccess(expectedResult)
      }
    }


    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpOtherAddress] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "issuedBy" -> 123456
      )

      json.validate[EtmpOtherAddress] `mustBe` a[JsError]
    }
  }
}
