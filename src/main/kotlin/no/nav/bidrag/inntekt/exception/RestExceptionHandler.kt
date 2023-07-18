package no.nav.bidrag.inntekt.exception

import no.nav.bidrag.commons.ExceptionLogger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@RestControllerAdvice
@Component
class RestExceptionHandler(private val exceptionLogger: ExceptionLogger)

sealed class RestResponse<T> {
    data class Success<T>(val body: T) : RestResponse<T>()
    data class Failure<T>(val message: String?, val statusCode: HttpStatusCode, val restClientException: RestClientException) : RestResponse<T>()
}

fun <T> RestTemplate.tryExchange(url: String, httpMethod: HttpMethod, httpEntity: HttpEntity<*>, responseType: Class<T>, fallbackBody: T): RestResponse<T> {
    return try {
        val response = exchange(url, httpMethod, httpEntity, responseType)
        RestResponse.Success(response.body ?: fallbackBody)
    } catch (e: HttpClientErrorException) {
        RestResponse.Failure("Message: ${e.message}", e.statusCode, e)
    } catch (e: HttpServerErrorException) {
        RestResponse.Failure("Message: ${e.message}", e.statusCode, e)
    }
}
