package no.nav.bidrag.inntekt.service

import no.nav.bidrag.behandling.felles.dto.grunnlag.SkattegrunnlagDto
import no.nav.bidrag.inntekt.dto.InntektPeriode
import no.nav.bidrag.inntekt.dto.SkattegrunnlagInntekt
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequest
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponse
import org.springframework.stereotype.Service

@Service
class InntektService() {

    fun transformerInntekter(transformerInntekterRequest: TransformerInntekterRequest): TransformerInntekterResponse {
        return TransformerInntekterResponse()
    }

    fun bestemInntektType() = "XX"
    // Skal returnere riktig inntekt-type (enum, f.eks. LTA) basert på input-kilde (f.eks. AINNTEKT)

    fun beregnInntektSiste3Mnd() = 0
    // Skal beregne sum inntekt siste 3 mnd pr inntekt-type, omregnet til årsinntekt

    fun beregnInntektSiste12Mnd() = 0
    // Skal beregne sum inntekt siste 12 mnd pr inntekt-type, (omregnet til årsinntekt)

    fun beregnInntektHittilIAar() = 0
    // Skal beregne sum inntekt hittil i år pr inntekt-type, omregnet til årsinntekt

    fun periodiserInntekter(): List<InntektPeriode> {
        // Skal periodisere inntekter basert på input
        return emptyList()
    }

    fun konverterSkattegrunnlag(skattegrunnlagListe: List<SkattegrunnlagDto>): List<SkattegrunnlagInntekt> {
        // Skal konvertere skattegrunnlag til LIGS eller KAPS og returnere en sum for hver pr år
        return emptyList()
    }
}
