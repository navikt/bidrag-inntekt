package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.bidrag.behandling.felles.dto.grunnlag.AinntektDto
import no.nav.bidrag.behandling.felles.dto.grunnlag.AinntektspostDto
import no.nav.bidrag.inntekt.dto.Inntekt
import no.nav.bidrag.inntekt.dto.InntektType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate

@Service
class AinntektService {

    // Summerer, grupperer og transformerer ainntekter
    fun beregnAinntekt(ainntektListeInn: List<AinntektDto>): List<Inntekt> {
        val objectMapper: ObjectMapper = jacksonObjectMapper()
        objectMapper.registerKotlinModule()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val ainntektMap = summerInntekter(ainntektListeInn)

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
                        sumInntekt = it.value.sumInntekt,
                        inntektPostListe = objectMapper.valueToTree(it.value.inntektPostListe)
                    )
                )
            }
        }

        return ainntektListeUt.sortedWith(compareBy({ it.inntektType.toString() }, { it.datoFra }))
    }

    // Summerer og grupperer ainntekter
    private fun summerInntekter(ainntektListeInn: List<AinntektDto>): Map<String, InntektSumPost> {
        val ainntektMap = mutableMapOf<String, InntektSumPost>()
        ainntektListeInn.forEach { ainntekt ->
            ainntekt.ainntektspostListe.forEach {
                val aar =
                    if (it.opptjeningsperiodeFra != null) it.opptjeningsperiodeFra!!.year.toString() else it.utbetalingsperiode!!.substring(0, 4)
                akkumulerPost(ainntektMap, it, aar)

                if (innenforAntallMnd(it, 3)) {
                    akkumulerPost(ainntektMap, it, KEY_3MND)
                }

                if (innenforAntallMnd(it, 12)) {
                    akkumulerPost(ainntektMap, it, KEY_12MND)
                }
            }
        }
        return ainntektMap.toMap()
    }

    // Summerer inntekter og legger til detaljposter til map
    private fun akkumulerPost(ainntektMap: MutableMap<String, InntektSumPost>, ainntektspost: AinntektspostDto, key: String) {
        val inntektSumPost = ainntektMap.getOrDefault(key, InntektSumPost(BigDecimal.ZERO, mutableListOf()))
        val sumInntekt = inntektSumPost.sumInntekt
        val inntektPostListe = inntektSumPost.inntektPostListe
        inntektPostListe.add(ainntektspost)
        ainntektMap[key] = InntektSumPost(sumInntekt.add(ainntektspost.belop), inntektPostListe)
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

    companion object {
        const val KEY_3MND = "3MND"
        const val KEY_12MND = "12MND"
    }
}
