package no.nav.bidrag.inntekt.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.models.examples.Example
import no.nav.bidrag.commons.util.OpenApiExample
import no.nav.bidrag.inntekt.InntektApi
import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequest
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponse
import no.nav.security.token.support.core.api.Protected
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

val eksempelfiler = listOf(
    "eksempel_request",
    "eksempel_request_aap",
    "eksempel_request_alle_ytelser",
    "eksempel_request_dagpenger",
)

@RestController
@Protected
class InntektController(private val inntektService: InntektApi) {
    @Bean
    fun eksempler(): List<OpenApiExample> = eksempelfiler.map { createExample(it) }
    private fun createExample(filename: String): OpenApiExample {
        val example = Example()
        example.value = InntektController::class.java.getResource("/testfiler/$filename.json")?.readText() ?: ""
        example.description = filename
        return OpenApiExample(
            example = example,
            method = HttpMethod.POST,
            path = TRANSFORMER_INNTEKTER,
        )
    }

    @PostMapping(TRANSFORMER_INNTEKTER)
    @Operation(security = [SecurityRequirement(name = "bearer-key")], summary = "Transformerer inntekter")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "400", description = "Feil i input"),
            ApiResponse(responseCode = "401", description = "Sikkerhetstoken mangler, er utløpt, eller av andre årsaker ugyldig"),
        ],
    )
    fun transformerInntekter(@RequestBody request: TransformerInntekterRequest): ResponseEntity<TransformerInntekterResponse> {
        val transformerteInntekter = inntektService.transformerInntekter(request)
        return ResponseEntity(transformerteInntekter, HttpStatus.OK)
    }

    companion object {
        const val TRANSFORMER_INNTEKTER = "/transformer"
    }
}
