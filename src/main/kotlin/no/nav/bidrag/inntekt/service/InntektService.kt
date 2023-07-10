package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.bidrag.behandling.felles.dto.grunnlag.AinntektDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.AinntektspostDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.SkattegrunnlagDto
import no.nav.bidrag.inntekt.dto.Inntekt
import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class InntektService {

    val objectMapper: ObjectMapper = jacksonObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())

    fun transformerInntekter(transformerInntekterRequestDto: TransformerInntekterRequestDto): TransformerInntekterResponseDto {
        return TransformerInntekterResponseDto(inntektListe = konverterAinntekt(transformerInntekterRequestDto.ainntektListe))
    }

    // Summerer, grupperer og transformerer ainntekter
    private fun konverterAinntekt(ainntektListeInn: List<AinntektDto>): List<Inntekt> {
        val ainntektMap = summerInntekterNy(ainntektListeInn)

        val ainntektListeUt = mutableListOf<Inntekt>()
        val dagensDato = LocalDate.now()
        val dagensAar = dagensDato.year

        ainntektMap.forEach {
            if (it.key.isNumeric()) {
                ainntektListeUt.add(
                    Inntekt(
                        inntektType = InntektType.AINNTEKT,
                        datoFra = LocalDate.parse(it.key + "-01-01"),
                        datoTil = if (it.key.toInt() == dagensAar) dagensDato else LocalDate.parse(it.key + "-01-01").plusYears(1),
//                        sumInntekt = it.value,
//                        inntektPostListe = ObjectMapper().createObjectNode()
                        sumInntekt = it.value.sumInntekt,
                        inntektPostListe = objectMapper.valueToTree(it.value.inntektPostListe)
                    )
                )
            } else {
                ainntektListeUt.add(
                    Inntekt(
                        inntektType = if (it.key == KEY_3MND) InntektType.AINNTEKT_BEREGNET_3MND else InntektType.AINNTEKT_BEREGNET_12MND,
                        datoFra = if (it.key == KEY_3MND) dagensDato.minusMonths(3) else dagensDato.minusYears(1),
                        datoTil = dagensDato,
//                        sumInntekt = it.value,
//                        inntektPostListe = ObjectMapper().createObjectNode()
                        sumInntekt = it.value.sumInntekt,
//                        inntektPostListe = ObjectMapper().valueToTree(it.value.inntektPostListe)
                        inntektPostListe = objectMapper.valueToTree(it.value.inntektPostListe)
                    )
                )
            }
        }

        return ainntektListeUt.sortedWith(compareBy({ it.inntektType.toString() }, { it.datoFra }))
    }

    // Summerer og grupperer ainntekter
    private fun summerInntekter(ainntektListeInn: List<AinntektDto>): Map<String, BigDecimal> {
        val ainntektMap = mutableMapOf<String, BigDecimal>()
        ainntektListeInn.forEach { ainntekt ->
            ainntekt.ainntektspostListe.forEach {
                val aar =
                    if (it.opptjeningsperiodeFra != null) it.opptjeningsperiodeFra!!.year.toString() else it.utbetalingsperiode!!.substring(0, 4)
                val summertAinntekt = ainntektMap.getOrDefault(aar, BigDecimal.ZERO).add(it.belop)
                ainntektMap[aar] = summertAinntekt

                if (innenforAntallMnd(it, 3)) {
                    val summertAinntekt3Mnd = ainntektMap.getOrDefault(KEY_3MND, BigDecimal.ZERO).add(it.belop)
                    ainntektMap[KEY_3MND] = summertAinntekt3Mnd
                }

                if (innenforAntallMnd(it, 12)) {
                    val summertAinntekt12Mnd = ainntektMap.getOrDefault(KEY_12MND, BigDecimal.ZERO).add(it.belop)
                    ainntektMap[KEY_12MND] = summertAinntekt12Mnd
                }
            }
        }
        return ainntektMap.toMap()
    }

    // Summerer og grupperer ainntekter
    private fun summerInntekterNy(ainntektListeInn: List<AinntektDto>): Map<String, InntektSumPost> {
        val ainntektMap = mutableMapOf<String, InntektSumPost>()
        ainntektListeInn.forEach { ainntekt ->
            ainntekt.ainntektspostListe.forEach {
                val aar =
                    if (it.opptjeningsperiodeFra != null) it.opptjeningsperiodeFra!!.year.toString() else it.utbetalingsperiode!!.substring(0, 4)
                val inntektSumPost = ainntektMap.getOrDefault(aar, InntektSumPost(BigDecimal.ZERO, mutableListOf()))
                val sumInntekt = inntektSumPost.sumInntekt
                val inntektPostListe = inntektSumPost.inntektPostListe
                inntektPostListe.add(it)
                ainntektMap[aar] = InntektSumPost(sumInntekt.add(it.belop), inntektPostListe)

                if (innenforAntallMnd(it, 3)) {
                    val inntektSumPost = ainntektMap.getOrDefault(KEY_3MND, InntektSumPost(BigDecimal.ZERO, mutableListOf()))
                    val sumInntekt = inntektSumPost.sumInntekt
                    val inntektPostListe = inntektSumPost.inntektPostListe
                    inntektPostListe.add(it)
                    ainntektMap[KEY_3MND] = InntektSumPost(sumInntekt.add(it.belop), inntektPostListe)
                }

                if (innenforAntallMnd(it, 12)) {
                    val inntektSumPost = ainntektMap.getOrDefault(KEY_12MND, InntektSumPost(BigDecimal.ZERO, mutableListOf()))
                    val sumInntekt = inntektSumPost.sumInntekt
                    val inntektPostListe = inntektSumPost.inntektPostListe
                    inntektPostListe.add(it)
                    ainntektMap[KEY_12MND] = InntektSumPost(sumInntekt.add(it.belop), inntektPostListe)
                }
            }
        }
        return ainntektMap.toMap()
    }

    // Sjekker om inntekt-dato er innenfor antall måneder (angitt som parameter)
    private fun innenforAntallMnd(ainntektspost: AinntektspostDto, antallMnd: Int): Boolean {
        val sammenlignMedDato = LocalDate.now().minusMonths(antallMnd.toLong())
        val inntektDato = ainntektspost.opptjeningsperiodeFra ?: LocalDate.parse(ainntektspost.utbetalingsperiode + "-01")
        return sammenlignMedDato.isBefore(inntektDato)
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
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

    companion object {
        const val KEY_3MND = "3MND"
        const val KEY_12MND = "12MND"
    }
}

data class InntektSumPost(
    val sumInntekt: BigDecimal,
    val inntektPostListe: MutableList<AinntektspostDto>
)
