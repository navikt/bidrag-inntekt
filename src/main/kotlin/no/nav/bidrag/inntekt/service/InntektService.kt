package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.inntekt.SECURE_LOGGER
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.tilJson
import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequest
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponse
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
class InntektService(
    val ainntektService: AinntektService,
    val skattegrunnlagService: SkattegrunnlagService,
    val kontantstøtteService: KontantstøtteService,
    val utvidetBarnetrygdOgSmåbarnstilleggService: UtvidetBarnetrygdOgSmåbarnstilleggService,
) {

    fun transformerInntekter(transformerInntekterRequest: TransformerInntekterRequest): TransformerInntekterResponse {
        val transformerInntekterResponse = TransformerInntekterResponse(
            versjon = "",
            summertMånedsinntektListe = ainntektService.beregnMaanedsinntekt(
                ainntektListeInn = transformerInntekterRequest.ainntektsposter,
            ),
            summertÅrsinntektListe = (
                ainntektService.beregnAarsinntekt(
                    ainntektListeInn = transformerInntekterRequest.ainntektsposter,
                ) +
//                    overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequest.overgangsstonadsliste) +
                    skattegrunnlagService.beregnSkattegrunnlag(
                        skattegrunnlagListe = transformerInntekterRequest.skattegrunnlagsliste,
                        inntektsrapportering = Inntektsrapportering.LIGNINGSINNTEKT,
                    ) +
                    skattegrunnlagService.beregnSkattegrunnlag(
                        skattegrunnlagListe = transformerInntekterRequest.skattegrunnlagsliste,
                        inntektsrapportering = Inntektsrapportering.KAPITALINNTEKT,
                    ) +
                    kontantstøtteService.beregnKontantstøtte(transformerInntekterRequest.kontantstøtteliste) +
                    utvidetBarnetrygdOgSmåbarnstilleggService.beregnUtvidetBarnetrygdOgSmåbarnstillegg(
                        transformerInntekterRequest.utvidetBarnetrygdOgSmåbarnstilleggliste,
                    )
                ),
        )

        SECURE_LOGGER.info("TransformerInntekterRequestDto: ${tilJson(transformerInntekterRequest.toString())}")
        SECURE_LOGGER.info("TransformerInntekterResponseDto: ${tilJson(transformerInntekterResponse.toString())}")

        return transformerInntekterResponse
    }
}

data class InntektSumPost(
    val sumInntekt: BigDecimal,
    val periodeFra: YearMonth,
    val periodeTil: YearMonth?,
    val inntektPostListe: MutableList<InntektPost>,
)

data class Periode(
    val periodeFra: YearMonth,
    val periodeTil: YearMonth?,
)
