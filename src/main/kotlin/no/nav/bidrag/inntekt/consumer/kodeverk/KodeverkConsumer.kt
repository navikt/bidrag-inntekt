package no.nav.bidrag.inntekt.consumer.kodeverk

import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.inntekt.consumer.InntektConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.consumer.kodeverk.api.HentKodeverkRequest
import no.nav.bidrag.inntekt.exception.RestResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RootUriTemplateHandler
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

private const val KODEVERK_CONTEXT = "/api/v1/kodeverk"

open class KodeverkConsumer(
//    private val restTemplate: HttpHeaderRestTemplate
    private val restTemplate: RestTemplate = HttpHeaderRestTemplate().apply {
        uriTemplateHandler = RootUriTemplateHandler("$KODEVERK_CONTEXT")
        addHeaderGenerator("Nav-Call-Id") { CorrelationId.generateTimestamped("bidrag-inntekt").get() }
        addHeaderGenerator("Nav-Consumer-Id") { "bidrag-inntekt" }
    }
) : InntektConsumer() {

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KodeverkConsumer::class.java)
    }

    open fun hentKodeverksverdier(request: HentKodeverkRequest): RestResponse<GetKodeverkKoderBetydningerResponse> {
        logger.info("Henter kodeverksverdier")

        logger.info("Request kodeverk: $KODEVERK_CONTEXT $request ${initHttpEntityKodeverk(request)} ")

        val restResponse = restTemplate.exchange(
            "/Summert skattegrunnlag/koder/betydninger",
            HttpMethod.GET,
            initHttpEntityKodeverk(request),
            GetKodeverkKoderBetydningerResponse::class.java,
            GetKodeverkKoderBetydningerResponse()
        )

//        logResponse(logger, restResponse)

        return RestResponse.Success(restResponse.body ?: GetKodeverkKoderBetydningerResponse())
    }
}
