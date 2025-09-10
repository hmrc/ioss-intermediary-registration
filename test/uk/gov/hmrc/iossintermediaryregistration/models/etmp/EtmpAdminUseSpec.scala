package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpAdminUseSpec extends BaseSpec {

  private val etmpAdminUse: EtmpAdminUse = arbitraryEtmpAdminUse.arbitrary.sample.value

  "EtmpAdminUseSpec" - {

    "must deserialise/serialise from and to EtmpAdminUse" - {

      "when the optional value is present" in {

        val json = Json.obj(
          "changeDate" -> etmpAdminUse.changeDate
        )

        val expectedResult: EtmpAdminUse = EtmpAdminUse(
          changeDate = etmpAdminUse.changeDate
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpAdminUse] `mustBe` JsSuccess(expectedResult)
      }

      "when the optional value is absent" in {

        val json = Json.obj()

        val expectedResult: EtmpAdminUse = EtmpAdminUse(
          changeDate = None
        )

        Json.toJson(expectedResult) `mustBe` json
        json.validate[EtmpAdminUse] `mustBe` JsSuccess(expectedResult)
      }
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "changeDate" -> "123456"
      )

      json.validate[EtmpAdminUse] `mustBe` a[JsError]
    }
  }
}
