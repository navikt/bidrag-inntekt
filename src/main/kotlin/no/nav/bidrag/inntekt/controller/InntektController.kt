package no.nav.bidrag.inntekt.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.inntekt.ISSUER
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import no.nav.bidrag.inntekt.service.InntektService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = ISSUER)
class InntektController(private val inntektService: InntektService) {

    @PostMapping(TRANSFORMER_INNTEKTER)
    @Operation(security = [SecurityRequirement(name = "bearer-key")], summary = "Transformerer inntekt")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "400", description = "Feil i input"),
            ApiResponse(responseCode = "401", description = "Sikkerhetstoken mangler, er utløpt, eller av andre årsaker ugyldig")
        ]
    )
    fun transformerInntekter(@RequestBody request: TransformerInntekterRequestDto): ResponseEntity<TransformerInntekterResponseDto> {
        val transformerteInntekter = inntektService.transformerInntekter(request)
        return ResponseEntity(transformerteInntekter, HttpStatus.OK)
    }

    companion object {
        const val TRANSFORMER_INNTEKTER = "/transformer"
        private val LOGGER = LoggerFactory.getLogger(InntektController::class.java)
    }
}
