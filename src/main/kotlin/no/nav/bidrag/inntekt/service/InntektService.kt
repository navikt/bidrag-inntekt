package no.nav.bidrag.inntekt.service

import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequestDto
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponseDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
class InntektService(
    val ainntektService: AinntektService,
    val skattegrunnlagService: SkattegrunnlagService,
    val overgangsstønadService: OvergangsstønadService
) {

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        return TransformerInntekterResponseDto(
            versjon = "",
            summertMaanedsinntektListe = ainntektService.beregnMaanedsinntekt(transformerInntekterRequestDto.ainntektListe),
            summertAarsinntektListe = (
                ainntektService.beregnAarsinntekt(transformerInntekterRequestDto.ainntektListe) +
                    overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequestDto.overgangsstonadListe)
                )

//            ligningsinntektListe = skattegrunnlagService.beregnLigs(transformerInntekterRequestDto.skattegrunnlagListe),
//            kapitalinntektListe = skattegrunnlagService.beregnKaps(transformerInntekterRequestDto.skattegrunnlagListe),
        )
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
