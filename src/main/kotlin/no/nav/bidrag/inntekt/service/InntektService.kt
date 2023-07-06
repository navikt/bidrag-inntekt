package no.nav.bidrag.inntekt.service

import no.nav.bidrag.behandling.felles.dto.grunnlag.SkattegrunnlagDto
import no.nav.bidrag.inntekt.dto.Inntekt
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import org.springframework.stereotype.Service

@Service
class InntektService() {

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        return TransformerInntekterResponseDto()
    }

    fun bestemInntektType() = "XX"
    // Skal returnere riktig inntekt-type (enum, f.eks. LTA) basert på input-kilde (f.eks. AINNTEKT)

    fun beregnAinntektSiste3Mnd() = 0
    // Skal beregne sum inntekt siste 3 mnd for Ainntekt, omregnet til årsinntekt

    fun beregnAinntektSiste12Mnd() = 0
    // Skal beregne sum inntekt siste 12 mnd for Ainntekt, (omregnet til årsinntekt)

    fun beregnInntektAar() = 0
    // Skal beregne sum inntekt for et kalenderår pr inntekt-type, omregnet til årsinntekt

    fun konverterSkattegrunnlag(skattegrunnlagListe: List<SkattegrunnlagDto>): List<Inntekt> {
        // Skal konvertere skattegrunnlag til LIGS eller KAPS og returnere en sum for hver pr år
        return emptyList()
    }
}
