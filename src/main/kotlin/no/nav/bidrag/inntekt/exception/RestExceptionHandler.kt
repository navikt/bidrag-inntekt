package no.nav.bidrag.inntekt.exception

import no.nav.bidrag.commons.ExceptionLogger
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Component
class RestExceptionHandler(private val exceptionLogger: ExceptionLogger)
