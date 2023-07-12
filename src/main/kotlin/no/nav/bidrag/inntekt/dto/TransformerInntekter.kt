package no.nav.bidrag.inntekt.dto

import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.databind.JsonNode
import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.transport.behandling.grunnlag.response.AinntektDto
import no.nav.bidrag.transport.behandling.grunnlag.response.KontantstotteDto
import no.nav.bidrag.transport.behandling.grunnlag.response.OvergangsstonadDto
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagDto
import no.nav.bidrag.transport.behandling.grunnlag.response.UtvidetBarnetrygdOgSmaabarnstilleggDto
import java.math.BigDecimal
import java.time.LocalDate

// TODO Legge til swagger-doc, default-verdier, flytte til bidrag-transport eller bidrag-behandling-felles-dto
// TODO Legge til evt. manuelle inntekter
// TODO Hva gjør vi med inntekter som ikke er i bruk?

data class TransformerInntekterRequestDto(
    @Schema(description = "Periodisert liste over inntekter fra Ainntekt")
    val ainntektListe: List<AinntektDto> = emptyList(),

    @Schema(description = "Periodisert liste over inntekter fra Sigrun")
    val skattegrunnlagListe: List<SkattegrunnlagDto> = emptyList(),

    @Schema(description = "Periodisert liste over utvidet barnetrygd og småbarnstillegg")
    val ubstListe: List<UtvidetBarnetrygdOgSmaabarnstilleggDto> = emptyList(),

    @Schema(description = "Periodisert liste over kontantstøtte")
    val kontantstotteListe: List<KontantstotteDto> = emptyList(),

    @Schema(description = "Periodisert liste over overgangsstønad")
    val overgangsstonadListe: List<OvergangsstonadDto> = emptyList()
)

data class TransformerInntekterResponseDto(
    @Schema(description = "Dato + commit hash", example = "20230705081501_68e71c7")
    val versjon: String = "",

    @Schema(description = "Liste over summerte ligningsinntekter (LIGS) pr år")
    val ligningsinntektListe: List<SkattegrunnlagInntekt> = emptyList(),

    @Schema(description = "Liste over summerte kapitalinntekter (KAPS) pr år")
    val kapitalinntektListe: List<SkattegrunnlagInntekt> = emptyList(),

    @Schema(description = "Liste over inntekter (periodisert)")
    val inntektListe: List<Inntekt> = emptyList(),

    @Schema(description = "Liste over overgangsstønad (periodisert)")
    val overgangsstonadListe: List<Overgangsstonad> = emptyList(),
)

data class SkattegrunnlagInntekt(
    @Schema(description = "Type inntekt fra Sigrun. Gyldige verdier er LIGNINGSINNTEKT (LIGS) og KAPITALINNTEKT (KAPS)", example = "LIGNINGSINNTEKT")
    val inntektType: InntektType,

    @Schema(description = "Hvilket år inntekten gjelder for", example = "2022")
    val aar: String,

    @Schema(description = "Summert inntekt for året", example = "500000")
    val sumInntekt: BigDecimal,

    @Schema(description = "Liste over inntektsposter som utgjør grunnlaget for summert inntekt")
    val inntektPostListe: List<SkattegrunnlagInntektPost>
)

data class SkattegrunnlagInntektPost(
    @Schema(description = "Navn på inntektspost (= teknisk navn fra Sigrun)", example = "annenArbeidsinntekt")
    val inntektPostNavn: String,

    @Schema(description = "Angir om inntektsposten skal legges til eller trekkes fra (PLUSS/MINUS)", example = "PLUSS")
    val plussEllerMinus: PlussMinus,

    @Schema(description = "Angir om posten er en sekkepost (true/false)", example = "false")
    val erSekkePost: Boolean,

    @Schema(description = "Beløpet tilhørende posten", example = "100000")
    val beløp: BigDecimal
)

data class Inntekt(
    @Schema(description = "Type inntekt", example = "AINNTEKT_BEREGNET_3MND")
    val inntektType: InntektType,

    @Schema(description = "Dato som inntekten gjelder fra", example = "2023-04-01")
    val periodeFra: LocalDate,

    @Schema(description = "Dato som inntekten gjelder til", example = "2023-07-01")
    val periodeTil: LocalDate?,

    @Schema(description = "Summert inntekt for perioden, omgjort til årsinntekt", example = "600000")
    val sumInntekt: BigDecimal,

    @Schema(description = "Liste over inntektsposter (generisk, avhengig av type) som utgjør grunnlaget for summert inntekt")
    @JsonRawValue
    val inntektPostListe: JsonNode
)

data class Overgangsstonad(
    @Schema(description = "Type inntekt", example = "OVERGANGSSTØNAD")
    val inntektType: InntektType,

    @Schema(description = "Dato som inntekten gjelder fra", example = "2023-04-01")
    val periodeFra: LocalDate,

    @Schema(description = "Dato som inntekten gjelder til", example = "2023-07-01")
    val periodeTil: LocalDate?,

    @Schema(description = "Summert inntekt for perioden, omgjort til årsinntekt", example = "600000")
    val sumInntekt: BigDecimal,

    @Schema(description = "Liste over hvilke overgangsstonadDto'er som er grunnlag for beregnet overgangsstønadinntekt")
    val overgangsstonadDtoListe: List<OvergangsstonadDto>
)

enum class InntektType(verdi: String) {
    AINNTEKT_BEREGNET_3MND("Ainntekt beregnet inntekt siste 3 mnd"),
    AINNTEKT_BEREGNET_12MND("Ainntekt beregnet inntekt siste 12 mnd"),
    AINNTEKT("Ainntekt"),
    LIGNINGSINNTEKT("Sigrun ligningsinntekt (LIGS)"),
    KAPITALINNTEKT("Sigrun kapitalinntekt (KAPS)"),
    UTVIDET_BARNETRYGD("Utvidet barnetrygd"),
    SMÅBARNSTILLEGG("Småbarnstillegg"),
    KONTANTSTØTTE("Kontantstøtte"),
    OVERGANGSSTØNAD("Overgangsstønad"),
    OVERGANGSSTØNAD_BEREGNET_3MND("Overgangsstønad beregnet inntekt siste 3 mnd"),
    OVERGANGSSTØNAD_BEREGNET_12MND("Overgangsstønad beregnet inntekt siste 12 mnd"),
}

enum class PlussMinus {
    PLUSS,
    MINUS
}
