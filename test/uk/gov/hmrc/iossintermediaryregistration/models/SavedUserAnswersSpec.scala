package uk.gov.hmrc.iossintermediaryregistration.models

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.models.SavedUserAnswers.standardFormat

class SavedUserAnswersSpec extends BaseSpec {

  private val savedUserAnswers: SavedUserAnswers = arbitrarySavedUserAnswers.arbitrary.sample.value

  "SavedUserAnswers" - {

    "must serialise/deserialise to and from SavedUserAnswers" in {

      val json = Json.obj(
        "vrn" -> savedUserAnswers.vrn,
        "data" -> savedUserAnswers.data,
        "lastUpdated" -> savedUserAnswers.lastUpdated
      )

      val expectedResult = SavedUserAnswers(
        vrn = savedUserAnswers.vrn,
        data = savedUserAnswers.data,
        lastUpdated = savedUserAnswers.lastUpdated
      )

      Json.toJson(expectedResult)(standardFormat) `mustBe` json
      json.validate[SavedUserAnswers](standardFormat) `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val expectedJson = Json.obj()

      expectedJson.validate[SavedUserAnswers] `mustBe` a[JsError]
    }

    "must handle invalid data during deserialization" in {

      val expectedJson = Json.obj(
        "vrn" -> 12345
      )

      expectedJson.validate[SavedUserAnswers] `mustBe` a[JsError]
    }
  }
}

