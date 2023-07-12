package no.nav.bidrag.inntekt.service


import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import no.nav.bidrag.transport.behandling.grunnlag.response.AinntektspostDto
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class InntektService(
    val ainntektService: AinntektService,
    val skattegrunnlagService: SkattegrunnlagService,
    val overgangsstønadService: OvergangsstønadService,
) {

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        return TransformerInntekterResponseDto(
            inntektListe = ainntektService.beregnAinntekt(transformerInntekterRequestDto.ainntektListe),
            ligningsinntektListe = skattegrunnlagService.beregnLigs(transformerInntekterRequestDto.skattegrunnlagListe),
            kapitalinntektListe = skattegrunnlagService.beregnKaps(transformerInntekterRequestDto.skattegrunnlagListe),
            overgangsstonadListe = overgangsstønadService.beregnOvergangsstønad(transformerInntekterRequestDto.overgangsstonadListe),
        )
    }
}

data class InntektSumPost(
    val sumInntekt: BigDecimal,
    val inntektPostListe: MutableList<AinntektspostDto>
)
