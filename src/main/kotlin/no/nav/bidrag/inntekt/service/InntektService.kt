package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.dto.InntektPost
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
class InntektService(
    val ainntektService: AinntektService
//    val skattegrunnlagService: SkattegrunnlagService,
//    val overgangsstønadService: OvergangsstønadService
) {

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        return TransformerInntekterResponseDto(
            summertAarsinntektListe = ainntektService.beregnAarsinntekt(transformerInntekterRequestDto.ainntektListe),
            summertMaanedsinntektListe = ainntektService.beregnMaanedsinntekt(transformerInntekterRequestDto.ainntektListe)
//            ligningsinntektListe = skattegrunnlagService.beregnLigs(transformerInntekterRequestDto.skattegrunnlagListe),
//            kapitalinntektListe = skattegrunnlagService.beregnKaps(transformerInntekterRequestDto.skattegrunnlagListe),
//            overgangsstonadListe = overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequestDto.overgangsstonadListe)
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
