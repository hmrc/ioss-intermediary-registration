package uk.gov.hmrc.iossintermediaryregistration.controllers

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, verifyNoInteractions, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.models.SavedUserAnswers
import uk.gov.hmrc.iossintermediaryregistration.models.SavedUserAnswers.standardFormat
import uk.gov.hmrc.iossintermediaryregistration.models.requests.SaveForLaterRequest
import uk.gov.hmrc.iossintermediaryregistration.models.responses.SaveForLaterResponse
import uk.gov.hmrc.iossintermediaryregistration.services.SaveForLaterService
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import scala.concurrent.Future

class SaveForLaterControllerSpec extends BaseSpec with BeforeAndAfterEach  {

  private val mockSaveForLaterService: SaveForLaterService = mock[SaveForLaterService]

  private val savedUserAnswers: SavedUserAnswers = arbitrarySavedUserAnswers.arbitrary.sample.value
  private val saveForLaterRequest: SaveForLaterRequest = arbitrarySaveForLaterRequest.arbitrary.sample.value
  private val saveForLaterResponse: SaveForLaterResponse = arbitrarySaveForLaterResponse.arbitrary.sample.value

  private lazy val postSaveForLaterRoute: String = routes.SaveForLaterController.post().url
  private lazy val getSaveForLaterRoute: String = routes.SaveForLaterController.get().url

  override def beforeEach(): Unit = {
    Mockito.reset(mockSaveForLaterService)
  }

  "SaveForLaterController" - {

    ".post" - {

      "must parse the request correctly, save the answers successfully and respond with Created with a response payload" in {

        when(mockSaveForLaterService.saveUserAnswers(any())) thenReturn savedUserAnswers.toFuture

        val application = applicationBuilder
          .overrides(bind[SaveForLaterService].toInstance(mockSaveForLaterService))
          .build()

        running(application) {
          val request = FakeRequest(POST, postSaveForLaterRoute)
            .withJsonBody(Json.toJson(saveForLaterRequest))

          val result = route(application, request).value

          status(result) `mustBe` CREATED
          contentAsJson(result) `mustBe` Json.toJson(savedUserAnswers)(standardFormat)
          verify(mockSaveForLaterService, times(1)).saveUserAnswers(eqTo(saveForLaterRequest))
        }
      }

      "must return a Bad Request when request body can not be parsed correctly" in {

        val application = applicationBuilder.build()

        running(application) {

          val request = FakeRequest(POST, postSaveForLaterRoute)
            .withJsonBody(Json.toJson("InvalidJson"))

          val result = route(application, request).value

          status(result) `mustBe` BAD_REQUEST
          verifyNoInteractions(mockSaveForLaterService)
        }
      }
      
      "must throw an Exception when Save For Later Service returns a Future.failed" in {

        when(mockSaveForLaterService.saveUserAnswers(any())) thenReturn Future.failed(Exception("error"))

        val application = applicationBuilder
          .overrides(bind[SaveForLaterService].toInstance(mockSaveForLaterService))
          .build()

        running(application) {
          val request = FakeRequest(POST, postSaveForLaterRoute)
            .withJsonBody(Json.toJson(saveForLaterRequest))

          val result = route(application, request).value

          whenReady(result.failed) { exp =>
            exp `mustBe` a[Exception]
            exp.getMessage `mustBe` "error"
          }
          verify(mockSaveForLaterService, times(1)).saveUserAnswers(eqTo(saveForLaterRequest))
        }
      }
    }

    ".get" - {

      "must return OK with a valid saved user answers response when they exist for the corresponding VRN" in {

        when(mockSaveForLaterService.getSavedUserAnswers(any())(any())) thenReturn Some(saveForLaterResponse).toFuture

        val application = applicationBuilder
          .overrides(bind[SaveForLaterService].toInstance(mockSaveForLaterService))
          .build()

        running(application) {

          val request = FakeRequest(GET, getSaveForLaterRoute)

          val result = route(application, request).value

          status(result) `mustBe` OK
          contentAsJson(result) `mustBe` Json.toJson(saveForLaterResponse)
          verify(mockSaveForLaterService, times(1)).getSavedUserAnswers(eqTo(vrn))(any())
        }
      }

      "must return NotFound if saved user answers do not exist for the corresponding VRN" in {

        when(mockSaveForLaterService.getSavedUserAnswers(any())(any())) thenReturn None.toFuture

        val application = applicationBuilder
          .overrides(bind[SaveForLaterService].toInstance(mockSaveForLaterService))
          .build()

        running(application) {

          val request = FakeRequest(GET, getSaveForLaterRoute)

          val result = route(application, request).value

          status(result) `mustBe` NOT_FOUND
          verify(mockSaveForLaterService, times(1)).getSavedUserAnswers(eqTo(vrn))(any())
        }
      }
    }

    ".delete" - {

      lazy val deleteSaveForLaterRoute: String = routes.SaveForLaterController.delete().url

      "must delete the saved user answers record for the corresponding VRN" in {

        when(mockSaveForLaterService.deleteSavedUserAnswers(any())) thenReturn true.toFuture

        val application = applicationBuilder
          .overrides(bind[SaveForLaterService].toInstance(mockSaveForLaterService))
          .build()

        running(application) {

          val request = FakeRequest(GET, deleteSaveForLaterRoute)

          val result = route(application, request).value

          status(result) `mustBe` OK
          verify(mockSaveForLaterService, times(1)).deleteSavedUserAnswers(eqTo(vrn))
        }
      }
    }
  }
}
