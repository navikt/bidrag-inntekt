package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.Overgangsstonad
import no.nav.bidrag.transport.behandling.grunnlag.response.OvergangsstonadDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class OvergangsstønadService() {

    fun beregnOvergangsstønad(overgangsstønadListe: List<OvergangsstonadDto>): List<Overgangsstonad> {

        val overgangsstønadResponseListe = mutableListOf<Overgangsstonad>()


        overgangsstønadListe.sortedWith(compareBy({ it.periodeFra }, { it.periodeTil })).forEach { overgangsstønadDto ->

            val overgangsstønadMap = mutableMapOf<String, OvergangsstønadSumPost>()

            akkumulerPost(overgangsstønadMap, overgangsstønadDto, overgangsstønadDto.periodeFra.year.toString())

            if (innenforAntallMnd(overgangsstønadDto, 3)) {
                akkumulerPost(overgangsstønadMap, overgangsstønadDto, KEY_3MND)
            }

            if (innenforAntallMnd(overgangsstønadDto, 12)) {
                akkumulerPost(overgangsstønadMap, overgangsstønadDto, KEY_12MND)
            }


            val overgangsstonadDtoListe = mutableListOf<OvergangsstonadDto>()

            var sumInntekt = BigDecimal.ZERO


            overgangsstønadResponseListe.add(
                Overgangsstonad(
                    inntektType = InntektType.OVERGANGSSTØNAD,
                    periodeFra = overgangsstønadDto.periodeFra,
                    periodeTil = overgangsstønadDto.periodeTil,
                    sumInntekt = overgangsstønadDto.belop.toBigDecimal(),
                    overgangsstonadDtoListe = overgangsstønadListe
                )
            )
        }

        return overgangsstønadResponseListe
    }

    // Summerer inntekter og legger til detaljposter til map
    private fun akkumulerPost(overgangsstønadMap: MutableMap<String, OvergangsstønadSumPost>, overgangsstønad: OvergangsstonadDto, key: String) {
        val overgangsstønadSumPost = overgangsstønadMap.getOrDefault(key, OvergangsstønadSumPost(BigDecimal.ZERO, mutableListOf()))
        val sumInntekt = overgangsstønadSumPost.sumInntekt
        val inntektPostListe = overgangsstønadSumPost.inntektPostListe
        inntektPostListe.add(overgangsstønad)
        overgangsstønadMap[key] = OvergangsstønadSumPost(sumInntekt.add(overgangsstønad.belop.toBigDecimal()), inntektPostListe)
    }



    // Sjekker om inntekt-dato er innenfor antall måneder (angitt som parameter)
    private fun innenforAntallMnd(overgangsstonadDto: OvergangsstonadDto, antallMnd: Int): Boolean {
        val sammenlignMedDato = LocalDate.now().minusMonths(antallMnd.toLong())
        return sammenlignMedDato.isBefore(overgangsstonadDto.periodeFra)
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }

    companion object {
        const val KEY_3MND = "3MND"
        const val KEY_12MND = "12MND"
    }

}

data class OvergangsstønadSumPost(
    val sumInntekt: BigDecimal,
    val inntektPostListe: MutableList<OvergangsstonadDto>
)





