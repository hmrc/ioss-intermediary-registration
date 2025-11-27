package uk.gov.hmrc.iossintermediaryregistration.services

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.test.Helpers.running
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.connectors.ChannelPreferenceConnector
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.channelPreference.ChannelPreferenceRequest
import uk.gov.hmrc.iossintermediaryregistration.models.external.{Event, EventData}
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class ChannelPreferenceServiceSpec extends BaseSpec with BeforeAndAfterEach {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private val enrolmentString = s"HMRC-IOSS-INT~IntNumber~$intNumber"
  private val email = "email@email.com"
  private val eventRequest = Event(UUID.randomUUID(), "subject", "groupId", EventData(email, Map("enrolment" -> enrolmentString)))

  private val mockChannelPreferenceConnector: ChannelPreferenceConnector = mock[ChannelPreferenceConnector]
  private val channelPreferenceService = new ChannelPreferenceService(mockChannelPreferenceConnector)

  override def beforeEach(): Unit = {
    reset(mockChannelPreferenceConnector)
  }

  "RegistrationServiceSpec#updatePreferences" - {

    "must call channel preference with correct INT number and reply when successful" in {

      val mockedResponse = HttpResponse(OK, "")

      when(mockChannelPreferenceConnector.updatePreferences(any())(any())) thenReturn mockedResponse.toFuture

      val app = applicationBuilder
        .build()

      running(app) {

        channelPreferenceService.updatePreferences(eventRequest).futureValue mustBe true

        val expectedRequest = ChannelPreferenceRequest("INT", intNumber, email, unusableStatus = true)
        verify(mockChannelPreferenceConnector, times(1)).updatePreferences(eqTo(expectedRequest))(any())
      }
    }

    "must reply with false when failure" in {

      val mockedResponse = HttpResponse(INTERNAL_SERVER_ERROR, "")

      when(mockChannelPreferenceConnector.updatePreferences(any())(any())) thenReturn mockedResponse.toFuture

      val app = applicationBuilder
        .build()

      running(app) {

        channelPreferenceService.updatePreferences(eventRequest).futureValue mustBe false

        val expectedRequest = ChannelPreferenceRequest("INT", intNumber, email, unusableStatus = true)
        verify(mockChannelPreferenceConnector, times(1)).updatePreferences(eqTo(expectedRequest))(any())
      }
    }
  }

}
