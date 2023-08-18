package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.exception.RestResponse
import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequestDto
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponseDto
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

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        val kodeverdierSkattegrunnlag = hentKodeverksverdierSkattegrunnlag()
        val kodeverdierLoennsbeskrivelse = hentKodeverksverdierLoennsbeskrivelse()

        return TransformerInntekterResponseDto(
            versjon = "",
            summertMaanedsinntektListe = ainntektService.beregnMaanedsinntekt(transformerInntekterRequestDto.ainntektListe, kodeverdierLoennsbeskrivelse),
            summertAarsinntektListe = (
                ainntektService.beregnAarsinntekt(transformerInntekterRequestDto.ainntektListe, kodeverdierLoennsbeskrivelse) +
                    overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequestDto.overgangsstonadListe) +
                    skattegrunnlagService.beregnLigs(transformerInntekterRequestDto.skattegrunnlagListe, kodeverdierSkattegrunnlag) +
                    skattegrunnlagService.beregnKaps(transformerInntekterRequestDto.skattegrunnlagListe, kodeverdierSkattegrunnlag)
                )
        )
    }

    fun hentKodeverksverdierSkattegrunnlag(): GetKodeverkKoderBetydningerResponse? {
        val kodeverk = "Summert skattegrunnlag"
        return when (
            val restResponseKodeverk =
                kodeverkConsumer.hentKodeverksverdier(kodeverk)
        ) {
            is RestResponse.Success -> {
                restResponseKodeverk.body
            }

            is RestResponse.Failure -> {
                logger.info("Feil under henting av kodeverksverdier/visningsnavn for skattegrunnlag")
                null
            }
        }
    }

    fun hentKodeverksverdierLoennsbeskrivelse(): GetKodeverkKoderBetydningerResponse? {
        val kodeverk = "Loennsbeskrivelse"
        return when (
            val restResponseKodeverk =
                kodeverkConsumer.hentKodeverksverdier(kodeverk)
        ) {
            is RestResponse.Success -> {
                restResponseKodeverk.body
            }

            is RestResponse.Failure -> {
                logger.info("Feil under henting av kodeverksverdier/visningsnavn for lønnsbeskrivelse")
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
