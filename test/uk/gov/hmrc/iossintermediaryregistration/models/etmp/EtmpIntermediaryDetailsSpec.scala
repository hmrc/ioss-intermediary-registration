package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import org.scalacheck.Gen
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpIntermediaryDetailsSpec extends BaseSpec {

  private val etmpOtherIossIntermediaryRegistrationsList: Seq[EtmpOtherIossIntermediaryRegistrations] = Gen.listOfN(3, arbitraryOtherIossIntermediaryRegistrations.arbitrary).sample.value

  "EtmpIntermediaryDetails" - {

    "must deserialise/serialise from and to EtmpBankDetails" in {

      val json = Json.obj(
        "otherIossIntermediaryRegistrations" -> etmpOtherIossIntermediaryRegistrationsList
      )

      val expectedResult: EtmpIntermediaryDetails = EtmpIntermediaryDetails(
        otherIossIntermediaryRegistrations = etmpOtherIossIntermediaryRegistrationsList
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[EtmpIntermediaryDetails] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpIntermediaryDetails] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "otherIossIntermediaryRegistrations" -> 123456
      )

      json.validate[EtmpIntermediaryDetails] `mustBe` a[JsError]
    }
  }
}
