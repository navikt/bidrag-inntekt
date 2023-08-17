package no.nav.bidrag.inntekt.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.inntekt.ISSUER
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.exception.RestResponse
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@ProtectedWithClaims(issuer = ISSUER)
class IntegrasjonsController(
    private val kodeverkConsumer: KodeverkConsumer

) {

    @GetMapping(HENT_FELLES_KODEVERK)
    @Operation(security = [SecurityRequirement(name = "bearer-key")], summary = "Kaller Felles Kodeverk og henter verdier")
    fun hentKodeverkSkattegrunnlag(kodeverk: String): ResponseEntity<GetKodeverkKoderBetydningerResponse> {
        return handleRestResponse(kodeverkConsumer.hentKodeverksverdier(kodeverk))
    }

    private fun <T> handleRestResponse(restResponse: RestResponse<T>): ResponseEntity<T> {
        return when (restResponse) {
            is RestResponse.Success -> ResponseEntity(restResponse.body, HttpStatus.OK)
            is RestResponse.Failure -> throw ResponseStatusException(restResponse.statusCode, restResponse.message)
        }
    }

    companion object {
        const val HENT_FELLES_KODEVERK = "/integrasjoner/kodeverk"
    }
}
