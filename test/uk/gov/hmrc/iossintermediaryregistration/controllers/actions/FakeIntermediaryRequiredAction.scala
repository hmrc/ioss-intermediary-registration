package uk.gov.hmrc.iossintermediaryregistration.controllers.actions

import play.api.mvc.{ActionRefiner, AnyContent, BodyParser, PlayBodyParsers, Request, Result}
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.domain.Vrn

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeIntermediaryRequiredAction @Inject()(bodyParsers: PlayBodyParsers)
                                              (implicit override val executionContext: ExecutionContext) extends IntermediaryRequiredAction {

  override protected def refine[A](request: AuthorisedMandatoryVrnRequest[A]): Future[Either[Result, AuthorisedMandatoryIntermediaryRequest[A]]] = {
    Future.successful(Right(AuthorisedMandatoryIntermediaryRequest(
      request = request,
      credentials = Credentials("12345-credId", "GGW"),
      userId = "id",
      vrn = Vrn(vrn = "123456789"),
      iossNumber = None,
      intermediaryNumber = "IntermediaryNumber"
    )))
  }
}