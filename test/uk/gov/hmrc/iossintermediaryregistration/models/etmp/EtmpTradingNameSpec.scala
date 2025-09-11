package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpTradingNameSpec extends BaseSpec {

  private val etmpTradingName: EtmpTradingName = arbitraryEtmpTradingName.arbitrary.sample.value

  "EtmpTradingName" - {

    "must deserialise/serialise from and to EtmpTradingName" in {

      val json = Json.obj(
        "tradingName" -> etmpTradingName.tradingName
      )

      val expectedResult: EtmpTradingName = EtmpTradingName(
        tradingName = etmpTradingName.tradingName
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[EtmpTradingName] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[EtmpTradingName] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "tradingName" -> 123456
      )

      json.validate[EtmpTradingName] `mustBe` a[JsError]
    }
  }
}
