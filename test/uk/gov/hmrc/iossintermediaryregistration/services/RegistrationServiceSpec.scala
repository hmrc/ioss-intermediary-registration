package uk.gov.hmrc.iossintermediaryregistration.services

import org.mockito.ArgumentMatchers.eq as eqTo
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers.running
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.connectors.RegistrationConnector
import uk.gov.hmrc.iossintermediaryregistration.models.*
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.responses.EtmpEnrolmentResponse
import uk.gov.hmrc.iossintermediaryregistration.testutils.RegistrationData.etmpRegistrationRequest
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import java.time.LocalDateTime

class RegistrationServiceSpec extends BaseSpec with BeforeAndAfterEach {

  private val mockRegistrationConnector: RegistrationConnector = mock[RegistrationConnector]
  private val registrationService = new RegistrationService(mockRegistrationConnector)

  override def beforeEach(): Unit = {
    reset(mockRegistrationConnector)
  }

  ".createRegistration" - {

    "must create registration request and return a successful ETMP enrolment response" in {

      val etmpEnrolmentResponse =
        EtmpEnrolmentResponse(
          processingDateTime = LocalDateTime.now(stubClock),
          formBundleNumber = "123456789",
          vrn = vrn.vrn,
          iossReference = "test",
          businessPartner = "test businessPartner"
        )

      when(mockRegistrationConnector.createRegistration(etmpRegistrationRequest)) thenReturn Right(etmpEnrolmentResponse).toFuture

      val app = applicationBuilder
        .build()

      running(app) {

        registrationService.createRegistration(etmpRegistrationRequest).futureValue mustBe Right(etmpEnrolmentResponse)
        verify(mockRegistrationConnector, times(1)).createRegistration(eqTo(etmpRegistrationRequest))
      }
    }
  }
}

