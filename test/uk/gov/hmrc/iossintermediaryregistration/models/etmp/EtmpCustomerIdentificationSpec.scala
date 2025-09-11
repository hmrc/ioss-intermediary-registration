package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpCustomerIdentificationSpec extends BaseSpec {

  private val etmpCustomerIdentification: EtmpCustomerIdentification = arbitraryEtmpCustomerIdentification.arbitrary.sample.value

  "EtmpCustomerIdentification" - {

    "must deserialise/serialise from and to EtmpCustomerIdentification" in {

      val json = Json.obj(
        "idType" -> etmpCustomerIdentification.idType,
        "idValue" -> etmpCustomerIdentification.idValue
      )

      val expectedResult: EtmpCustomerIdentification = EtmpCustomerIdentification(
        idType = etmpCustomerIdentification.idType,
        idValue = etmpCustomerIdentification.idValue
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[EtmpCustomerIdentification] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpCustomerIdentification] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "idValue" -> 123456
      )

      json.validate[EtmpCustomerIdentification] `mustBe` a[JsError]
    }
  }
}
