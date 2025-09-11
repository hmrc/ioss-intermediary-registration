package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpExclusionSpec extends BaseSpec {

  private val etmpExclusion: EtmpExclusion = arbitraryEtmpExclusion.arbitrary.sample.value

  "EtmpExclusion" - {

    "must deserialise/serialise from and to EtmpExclusion" in {

      val json = Json.obj(
        "exclusionReason" -> etmpExclusion.exclusionReason,
        "effectiveDate" -> etmpExclusion.effectiveDate,
        "decisionDate" -> etmpExclusion.decisionDate,
        "quarantine" -> etmpExclusion.quarantine
      )

      val expectedResult: EtmpExclusion = EtmpExclusion(
        exclusionReason = etmpExclusion.exclusionReason,
        effectiveDate = etmpExclusion.effectiveDate,
        decisionDate = etmpExclusion.decisionDate,
        quarantine = etmpExclusion.quarantine
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[EtmpExclusion] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpExclusion] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "exclusionReason" -> 123456
      )

      json.validate[EtmpExclusion] `mustBe` a[JsError]
    }
  }
}
