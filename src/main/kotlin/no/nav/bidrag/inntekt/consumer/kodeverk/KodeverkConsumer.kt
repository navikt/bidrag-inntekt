package no.nav.bidrag.inntekt.consumer.kodeverk

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.inntekt.aop.RestResponse
import no.nav.bidrag.inntekt.aop.tryExchange
import no.nav.bidrag.inntekt.consumer.InntektConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod

open class KodeverkConsumer(
    private val restTemplate: HttpHeaderRestTemplate,
) : InntektConsumer() {

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KodeverkConsumer::class.java)
    }

    @Cacheable
    open fun hentKodeverksverdier(kodeverk: String): RestResponse<GetKodeverkKoderBetydningerResponse> {
        val kodeverkContext = "/api/v1/kodeverk/$kodeverk/koder/betydninger?ekskluderUgyldige=true&spraak=nb"

        logger.info("Henter kodeverksverdier med request: $kodeverkContext")

        val restResponse = restTemplate.tryExchange(
            kodeverkContext,
            HttpMethod.GET,
            initHttpEntityKodeverk(null),
            GetKodeverkKoderBetydningerResponse::class.java,
            GetKodeverkKoderBetydningerResponse(),
        )

        logResponse(logger, restResponse)

        return restResponse
    }
}
