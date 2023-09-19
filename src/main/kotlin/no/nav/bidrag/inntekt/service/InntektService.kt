package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.SECURE_LOGGER
import no.nav.bidrag.inntekt.aop.RestResponse
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.LOENNSBESKRIVELSE
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.SUMMERT_SKATTEGRUNNLAG
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.tilJson
import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequest
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
class InntektService(
    val ainntektService: AinntektService,
    val skattegrunnlagService: SkattegrunnlagService,
    val overgangsstønadService: OvergangsstønadService,
    val kodeverkConsumer: KodeverkConsumer
) {

    fun transformerInntekter(transformerInntekterRequest: TransformerInntekterRequest): TransformerInntekterResponse {
        val kodeverdierSkattegrunnlag = hentKodeverksverdier(SUMMERT_SKATTEGRUNNLAG)
        val kodeverdierLoennsbeskrivelse = hentKodeverksverdier(LOENNSBESKRIVELSE)

        val transformerInntekterResponse = TransformerInntekterResponse(
            versjon = "",
            summertMaanedsinntektListe = ainntektService.beregnMaanedsinntekt(
                transformerInntekterRequest.ainntektListe,
                kodeverdierLoennsbeskrivelse
            ),
            summertAarsinntektListe = (
                ainntektService.beregnAarsinntekt(transformerInntekterRequest.ainntektListe, kodeverdierLoennsbeskrivelse) +
                    overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequest.overgangsstonadListe) +
                    skattegrunnlagService.beregnSkattegrunnlag(
                        transformerInntekterRequest.skattegrunnlagListe,
                        kodeverdierSkattegrunnlag,
                        InntektBeskrivelse.LIGNINGSINNTEKT
                    ) +
                    skattegrunnlagService.beregnSkattegrunnlag(
                        transformerInntekterRequest.skattegrunnlagListe,
                        kodeverdierSkattegrunnlag,
                        InntektBeskrivelse.KAPITALINNTEKT
                    )
                )

        )

        SECURE_LOGGER.info("TransformerInntekterRequestDto: ${tilJson(transformerInntekterRequest.toString())}")
        SECURE_LOGGER.info("TransformerInntekterResponseDto: ${tilJson(transformerInntekterResponse.toString())}")

        return transformerInntekterResponse
    }

    private fun hentKodeverksverdier(kodeverk: String): GetKodeverkKoderBetydningerResponse? {
        return when (
            val restResponseKodeverk =
                kodeverkConsumer.hentKodeverksverdier(kodeverk)
        ) {
            is RestResponse.Success -> {
                restResponseKodeverk.body
            }

            is RestResponse.Failure -> {
                logger.warn("Feil under henting av kodeverksverdier/visningsnavn for $kodeverk")
                null
            }
        }
    }

    companion object {

        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(InntektService::class.java)
    }
}

data class InntektSumPost(
    val sumInntekt: BigDecimal,
    val periodeFra: YearMonth,
    val periodeTil: YearMonth?,
    val inntektPostListe: MutableList<InntektPost>
)

data class Periode(
    val periodeFra: YearMonth,
    val periodeTil: YearMonth?
)
