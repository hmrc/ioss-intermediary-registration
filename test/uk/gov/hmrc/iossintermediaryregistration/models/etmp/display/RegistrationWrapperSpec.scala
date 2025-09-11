package uk.gov.hmrc.iossintermediaryregistration.models.etmp.display

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.models.des.VatCustomerInfo

class RegistrationWrapperSpec extends BaseSpec {

  private val vatCustomerInfo: VatCustomerInfo = arbitraryVatCustomerInfo.arbitrary.sample.value
  private val etmpDisplayRegistration: EtmpDisplayRegistration = arbitraryEtmpDisplayRegistration.arbitrary.sample.value

  private val registrationWrapper: RegistrationWrapper = RegistrationWrapper(
    vatInfo = vatCustomerInfo,
    etmpDisplayRegistration = etmpDisplayRegistration
  )

  "RegistrationWrapper" - {

    "must deserialise from RegistrationWrapper" in {

      val json: JsValue = Json.toJson(registrationWrapper)

      val expectedResult = RegistrationWrapper(
        vatInfo = vatCustomerInfo,
        etmpDisplayRegistration = etmpDisplayRegistration
      )

      Json.toJson(expectedResult) `mustBe` json
    }

    "must serialise to RegistrationWrapper" in {

      val registrationWrapperWrites: Writes[RegistrationWrapper] = {
        (
          (__ \ "vatInfo").write[VatCustomerInfo] and
            (__ \ "etmpDisplayRegistration").write[EtmpDisplayRegistration](etmpDisplayRegistrationWrites)
          )(registrationWrapper => Tuple.fromProductTyped(registrationWrapper))
      }

      val json: JsValue = Json.toJson(registrationWrapper)(registrationWrapperWrites)

      val expectedResult = RegistrationWrapper(
        vatInfo = vatCustomerInfo,
        etmpDisplayRegistration = etmpDisplayRegistration
      )

      json.validate[RegistrationWrapper] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val json = Json.obj()

      json.validate[RegistrationWrapper] `mustBe` a[JsError]
    }

    "must handle invalid fields during deserialization" in {

      val json = Json.obj(
        "vatInfo" -> 123456
      )

      json.validate[RegistrationWrapper] `mustBe` a[JsError]
    }
  }
}
