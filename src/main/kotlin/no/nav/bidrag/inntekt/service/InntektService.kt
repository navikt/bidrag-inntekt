package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.exception.RestResponse
import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequestDto
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponseDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InntektService(
    val ainntektService: AinntektService,
    val skattegrunnlagService: SkattegrunnlagService,
    val overgangsstønadService: OvergangsstønadService,
    val kodeverkConsumer: KodeverkConsumer
) {

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        val kodeverdierSkattegrunnlag = hentKodeverksverdier(SUMMERT_SKATTEGRUNNLAG)
        val kodeverdierLoennsbeskrivelse = hentKodeverksverdier(LOENNSBESKRIVELSE)

        return TransformerInntekterResponseDto(
            versjon = "",
            summertMaanedsinntektListe = ainntektService.beregnMaanedsinntekt(
                transformerInntekterRequestDto.ainntektListe,
                kodeverdierLoennsbeskrivelse
            ),
            summertAarsinntektListe = (
                ainntektService.beregnAarsinntekt(transformerInntekterRequestDto.ainntektListe, kodeverdierLoennsbeskrivelse) +
                    overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequestDto.overgangsstonadListe) +
                    skattegrunnlagService.beregnSkattegrunnlag(
                        transformerInntekterRequestDto.skattegrunnlagListe,
                        kodeverdierSkattegrunnlag,
                        InntektBeskrivelse.LIGNINGSINNTEKT
                    ) +
                    skattegrunnlagService.beregnSkattegrunnlag(
                        transformerInntekterRequestDto.skattegrunnlagListe,
                        kodeverdierSkattegrunnlag,
                        InntektBeskrivelse.KAPITALINNTEKT
                    )
                )
        )
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

        const val SUMMERT_SKATTEGRUNNLAG = "Summert skattegrunnlag"
        const val LOENNSBESKRIVELSE = "Loennsbeskrivelse"
    }
}
