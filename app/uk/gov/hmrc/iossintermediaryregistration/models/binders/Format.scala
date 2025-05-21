package uk.gov.hmrc.iossintermediaryregistration.models.binders

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Format {
  
  val eisDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z")
    .withLocale(Locale.UK)
    .withZone(ZoneId.of("GMT"))

}