package no.nav.bidrag.inntekt.dto

import no.nav.bidrag.behandling.felles.dto.grunnlag.AinntektDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.BarnetilleggDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.BarnetilsynDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.KontantstotteDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.OvergangsstonadDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.SkattegrunnlagDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.UtvidetBarnetrygdOgSmaabarnstilleggDto
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Year

// TODO Legge til swagger-doc, default-verdier, flytte til bidrag-transport eller bidrag-behandling-felles-dto
// TODO Legge til evt. manuelle inntekter

data class TransformerInntekterRequest(
    val ainntektListe: List<AinntektDto> = emptyList(),
    val skattegrunnlagListe: List<SkattegrunnlagDto> = emptyList(),
    val ubstListe: List<UtvidetBarnetrygdOgSmaabarnstilleggDto> = emptyList(),
    val barnetilleggListe: List<BarnetilleggDto> = emptyList(),
    val kontantstotteListe: List<KontantstotteDto> = emptyList(),
    val barnetilsynListe: List<BarnetilsynDto> = emptyList(),
    val overgangsstonadListe: List<OvergangsstonadDto> = emptyList()
)

data class TransformerInntekterResponse(
    val versjon: String = "",
    val skattegrunnlagInntektListe: List<SkattegrunnlagInntekt> = emptyList(),
    val periodisertInntektListe: List<PeriodisertInntekt> = emptyList()
)

data class SkattegrunnlagInntekt(
    val aar: Year,
    val sumSkattegrunnlag: BigDecimal,
    val sumKapitalinntekt: BigDecimal
)

data class PeriodisertInntekt(
    val inntektType: String,
    val beregnetInntektSiste3Mnd: BigDecimal,
    val beregnetInntektSiste12Mnd: BigDecimal,
    val beregnetInntektHittilIAar: BigDecimal,
    val inntektPeriodeListe: List<InntektPeriode>
)

data class InntektPeriode(
    val datoFra: LocalDate,
    val datoTil: LocalDate,
    val belop: BigDecimal
)
