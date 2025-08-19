package uk.gov.hmrc.iossintermediaryregistration.services

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, verifyNoInteractions, when}
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.connectors.GetVatInfoConnector
import uk.gov.hmrc.iossintermediaryregistration.models.SavedUserAnswers
import uk.gov.hmrc.iossintermediaryregistration.models.requests.SaveForLaterRequest
import uk.gov.hmrc.iossintermediaryregistration.models.responses.{NotFound, SaveForLaterResponse}
import uk.gov.hmrc.iossintermediaryregistration.repositories.SaveForLaterRepository
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

class SaveForLaterServiceSpec extends BaseSpec with BeforeAndAfterEach {

  private implicit val hc: HeaderCarrier = new HeaderCarrier()

  private val mockSaveForLaterRepository: SaveForLaterRepository = mock[SaveForLaterRepository]
  private val mockGetVatInfoConnector: GetVatInfoConnector = mock[GetVatInfoConnector]

  private val saveForLaterRequest: SaveForLaterRequest = arbitrarySaveForLaterRequest.arbitrary.sample.value

  private val savedUserAnswers: SavedUserAnswers = SavedUserAnswers(
    vrn = saveForLaterRequest.vrn,
    data = saveForLaterRequest.data,
    lastUpdated = Instant.now(stubClock)
  )

  override def beforeEach(): Unit = {
    Mockito.reset(mockSaveForLaterRepository)
    Mockito.reset(mockGetVatInfoConnector)
  }

  "SaveForLaterService" - {

    ".saveUserAnswers" - {

      "must create a Save User Answers and save them when invoked" in {

        when(mockSaveForLaterRepository.set(any())) thenReturn savedUserAnswers.toFuture

        val service: SaveForLaterService = new SaveForLaterService(mockSaveForLaterRepository, mockGetVatInfoConnector, stubClock)

        val result = service.saveUserAnswers(saveForLaterRequest).futureValue

        result `mustBe` savedUserAnswers
        verify(mockSaveForLaterRepository, times(1)).set(any())
        verifyNoInteractions(mockGetVatInfoConnector)
      }
    }

    ".getSavedUserAnswers" - {

      "must retrieve Saved User Answers when they exist for a specific VRN" in {

        when(mockSaveForLaterRepository.get(any())) thenReturn Some(savedUserAnswers).toFuture
        when(mockGetVatInfoConnector.getVatCustomerDetails(any())(any())) thenReturn Right(vatCustomerInfo).toFuture

        val saveForLaterResponse: SaveForLaterResponse = SaveForLaterResponse(savedUserAnswers, vatCustomerInfo)

        val service: SaveForLaterService = new SaveForLaterService(mockSaveForLaterRepository, mockGetVatInfoConnector, stubClock)

        val result = service.getSavedUserAnswers(savedUserAnswers.vrn).futureValue

        result `mustBe` Some(saveForLaterResponse)
        verify(mockGetVatInfoConnector, times(1)).getVatCustomerDetails(eqTo(savedUserAnswers.vrn))(any())
        verify(mockSaveForLaterRepository, times(1)).get(eqTo(savedUserAnswers.vrn))
      }

      "must return None when Saved User Answers do not exist for a specific VRN" in {

        when(mockSaveForLaterRepository.get(any())) thenReturn None.toFuture

        val service: SaveForLaterService = new SaveForLaterService(mockSaveForLaterRepository, mockGetVatInfoConnector, stubClock)

        val result = service.getSavedUserAnswers(savedUserAnswers.vrn).futureValue

        result `mustBe` None
        verify(mockSaveForLaterRepository, times(1)).get(eqTo(savedUserAnswers.vrn))
        verifyNoInteractions(mockGetVatInfoConnector)
      }

      "must throw an Exception when Saved User Answers exist for a specific VRN but VatCustomerInfo retrieval fails" in {

        val errorMessage: String = s"There was an error retrieving VatCustomerInfo for the provided VRN with errors: $NotFound."

        when(mockSaveForLaterRepository.get(any())) thenReturn Some(savedUserAnswers).toFuture
        when(mockGetVatInfoConnector.getVatCustomerDetails(any())(any())) thenReturn Left(NotFound).toFuture

        val service: SaveForLaterService = new SaveForLaterService(mockSaveForLaterRepository, mockGetVatInfoConnector, stubClock)

        val result = service.getSavedUserAnswers(savedUserAnswers.vrn).failed

        whenReady(result) { exp =>
          exp `mustBe` a[IllegalStateException]
          exp.getMessage `mustBe` errorMessage
        }
        verify(mockGetVatInfoConnector, times(1)).getVatCustomerDetails(eqTo(savedUserAnswers.vrn))(any())
        verify(mockSaveForLaterRepository, times(1)).get(eqTo(savedUserAnswers.vrn))
      }
    }

    ".deleteSavedUserAnswers" - {

      "must delete the Saved User Answers record for a specific VRN" in {

        when(mockSaveForLaterRepository.clear(any())) thenReturn true.toFuture

        val service: SaveForLaterService = new SaveForLaterService(mockSaveForLaterRepository, mockGetVatInfoConnector, stubClock)

        val result = service.deleteSavedUserAnswers(savedUserAnswers.vrn).futureValue

        result `mustBe` true
        verify(mockSaveForLaterRepository, times(1)).clear(eqTo(savedUserAnswers.vrn))
        verifyNoInteractions(mockGetVatInfoConnector)
      }
    }
  }
}
