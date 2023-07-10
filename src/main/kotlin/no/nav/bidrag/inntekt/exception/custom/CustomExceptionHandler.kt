package no.nav.bidrag.inntekt.exception.custom

import no.nav.bidrag.commons.ExceptionLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Component
class CustomExceptionHandler(private val exceptionLogger: ExceptionLogger) {

    @ResponseBody
    @ExceptionHandler(UgyldigInputException::class)
    protected fun handleUgyldigInputException(e: UgyldigInputException): ResponseEntity<*> {
        exceptionLogger.logException(e, "CustomExceptionHandler")
        return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
    }
}
