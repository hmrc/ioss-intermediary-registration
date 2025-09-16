package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpClientDetailsSpec extends BaseSpec {

  private val etmpClientDetails: EtmpClientDetails = arbitraryEtmpClientDetails.arbitrary.sample.value

  "EtmpClientDetails" - {

    "must deserialise/serialise from and to EtmpClientDetails" in {

      val json = Json.obj(
        "clientName" -> etmpClientDetails.clientName,
        "clientIossID" -> etmpClientDetails.clientIossID,
        "clientExcluded" -> etmpClientDetails.clientExcluded
      )

      val expectedResult: EtmpClientDetails = EtmpClientDetails(
        clientName = etmpClientDetails.clientName,
        clientIossID = etmpClientDetails.clientIossID,
        clientExcluded = etmpClientDetails.clientExcluded
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[EtmpClientDetails] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpClientDetails] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "clientExcluded" -> 123456
      )

      json.validate[EtmpClientDetails] `mustBe` a[JsError]
    }
  }
}
