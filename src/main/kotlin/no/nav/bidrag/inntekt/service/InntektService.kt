package no.nav.bidrag.inntekt.service

import no.nav.bidrag.behandling.felles.dto.grunnlag.AinntektspostDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class InntektService(
    val ainntektService: AinntektService,
    val skattegrunnlagService: SkattegrunnlagService
) {
  
    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        return TransformerInntekterResponseDto(
            inntektListe = ainntektService.beregnAinntekt(transformerInntekterRequestDto.ainntektListe),
            ligningsinntektListe = skattegrunnlagService.beregnLigs(transformerInntekterRequestDto.skattegrunnlagListe),
            kapitalinntektListe = skattegrunnlagService.beregnKaps(transformerInntekterRequestDto.skattegrunnlagListe)
        )
    }
}

data class InntektSumPost(
    val sumInntekt: BigDecimal,
    val inntektPostListe: MutableList<AinntektspostDto>
)
