package no.nav.bidrag.inntekt.consumer.kodeverk

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.inntekt.consumer.InntektConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.consumer.kodeverk.api.HentKodeverkRequest
import no.nav.bidrag.inntekt.exception.RestResponse
import no.nav.bidrag.inntekt.exception.tryExchange
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod

private const val KODEVERK_CONTEXT = "/api/v1/kodeverk/Summert skattegrunnlag/koder/betydninger"

open class KodeverkConsumer(
    private val restTemplate: HttpHeaderRestTemplate
) : InntektConsumer() {

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KodeverkConsumer::class.java)
    }

    @Cacheable
    open fun hentKodeverksverdier(request: HentKodeverkRequest): RestResponse<GetKodeverkKoderBetydningerResponse> {
        logger.info("Henter kodeverksverdier")

        logger.info("Request kodeverk: $KODEVERK_CONTEXT $request ${initHttpEntityKodeverk(request)} ")

        val restResponse = restTemplate.tryExchange(
            KODEVERK_CONTEXT,
            HttpMethod.GET,
            initHttpEntityKodeverk(request),
            GetKodeverkKoderBetydningerResponse::class.java,
            GetKodeverkKoderBetydningerResponse()
        )

        logResponse(logger, restResponse)

        return restResponse
    }
}
