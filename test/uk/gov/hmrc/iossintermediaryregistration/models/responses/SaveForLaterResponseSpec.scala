package uk.gov.hmrc.iossintermediaryregistration.models.responses

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class SaveForLaterResponseSpec extends BaseSpec {

  private val saveForLaterResponse: SaveForLaterResponse = arbitrarySaveForLaterResponse.arbitrary.sample.value

  "SaveForLaterResponse" - {
    
    "must serialise/deserialise to and from SaveForLaterResponse" in {

      val json = Json.obj(
        "vrn" -> saveForLaterResponse.vrn,
        "data" -> saveForLaterResponse.data,
        "vatInfo" -> saveForLaterResponse.vatInfo,
        "lastUpdated" -> saveForLaterResponse.lastUpdated
      )

      val expectedResult = SaveForLaterResponse(
        vrn = saveForLaterResponse.vrn,
        data = saveForLaterResponse.data,
        vatInfo = saveForLaterResponse.vatInfo,
        lastUpdated = saveForLaterResponse.lastUpdated
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[SaveForLaterResponse] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val expectedJson = Json.obj()

      expectedJson.validate[SaveForLaterResponse] `mustBe` a[JsError]
    }

    "must handle invalid data during deserialization" in {

      val expectedJson = Json.obj(
        "vrn" -> 12345
      )

      expectedJson.validate[SaveForLaterResponse] `mustBe` a[JsError]
    }
  }
}
