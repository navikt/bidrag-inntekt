package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.dto.InntektPost
import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.SummertÅrsinntekt
import no.nav.bidrag.transport.behandling.grunnlag.response.OvergangsstonadDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth

@Service
class OvergangsstønadService() {

    fun beregnOvergangsstønad(overgangsstønadListeInn: List<OvergangsstonadDto>): List<SummertÅrsinntekt> {

        val overgangsstønadListeInnSortert = overgangsstønadListeInn.sortedWith(compareBy({ it.periodeFra }, { it.periodeTil }))

        val overgangsstønadResponseListe = mutableListOf<SummertÅrsinntekt>()

        val overgangsstønadMap = mutableMapOf<String, SummertÅrsinntekt>()

        val førstePeriodeFra =
            YearMonth.of(
                overgangsstønadListeInnSortert.first().periodeFra.year,
                overgangsstønadListeInnSortert.first().periodeFra.month)

        val periodeFra3mnd =
            YearMonth.of(
                overgangsstønadListeInnSortert.last().periodeFra.year,
                overgangsstønadListeInnSortert.last().periodeFra.month
            ).minusMonths(2)
        val periodeFra12mnd =
            YearMonth.of(
                overgangsstønadListeInnSortert.last().periodeFra.year,
                overgangsstønadListeInnSortert.last().periodeFra.month
            ).minusMonths(11)

        val periodeFraSisteMottatteOvergangsstønad = YearMonth.of(
            overgangsstønadListeInnSortert.last().periodeFra.year,
            overgangsstønadListeInnSortert.last().periodeFra.month
        )

        overgangsstønadListeInnSortert.forEach { overgangsstønadDto ->

            akkumulerPost(overgangsstønadMap, overgangsstønadDto, overgangsstønadDto.periodeFra.year.toString())

            if (innenforAntallMnd(overgangsstønadDto, periodeFraSisteMottatteOvergangsstønad, 3)) {
                akkumulerPost(overgangsstønadMap, overgangsstønadDto, KEY_3MND)
            }

            if (innenforAntallMnd(overgangsstønadDto, periodeFraSisteMottatteOvergangsstønad, 12)) {
                akkumulerPost(overgangsstønadMap, overgangsstønadDto, KEY_12MND)
            }
        }

        val dagensDato = YearMonth.now()
        val dagensAar = dagensDato.year


        overgangsstønadMap.forEach {
            if (it.key.isNumeric()) {
                overgangsstønadResponseListe.add(
                    SummertÅrsinntekt(
                        inntektType = InntektType.OVERGANGSSTØNAD,
                        visningsnavn = InntektType.OVERGANGSSTØNAD.toString(),
                        referanse = "",
                        periodeFra = it.value.periodeFra,
//                        periodeFra = YearMonth.parse(it.key + "-01"),
                        periodeTil = if (it.key.toInt() == dagensAar) dagensDato else YearMonth.parse(it.key + "-01").plusYears(1),
                        sumInntekt = it.value.sumInntekt,
                        inntektPostListe = it.value.inntektPostListe
                    )
                )
            } else {
                overgangsstønadResponseListe.add(
                    SummertÅrsinntekt(
                        inntektType = if (it.key == KEY_3MND) InntektType.OVERGANGSSTØNAD_BEREGNET_3MND else InntektType.OVERGANGSSTØNAD_BEREGNET_12MND,
                        visningsnavn = InntektType.OVERGANGSSTØNAD.toString(),
                        referanse = "",
                        periodeFra = if (it.key == KEY_3MND) periodeFra3mnd else periodeFra12mnd,
                        periodeTil = null,
                        sumInntekt = if (it.key == KEY_3MND) it.value.sumInntekt
                            .divide(BigDecimal.valueOf(3))
                            .multiply(BigDecimal.valueOf(12)).setScale(0, RoundingMode.HALF_UP)
                        else it.value.sumInntekt,
                        inntektPostListe = it.value.inntektPostListe
                    )
                )

            }


        }

        return overgangsstønadResponseListe.sortedWith(compareBy({ it.inntektType }, { it.periodeFra }, { it.periodeTil }))
    }


    // Summerer inntekter og legger til detaljposter til map
    private fun akkumulerPost(overgangsstønadMap: MutableMap<String, SummertÅrsinntekt>, overgangsstønad: OvergangsstonadDto, key: String) {
        val overgangsstønadMapPost = overgangsstønadMap.getOrDefault(
            key,
            SummertÅrsinntekt(InntektType.OVERGANGSSTØNAD, "", "", BigDecimal.ZERO,
                YearMonth.of(overgangsstønad.periodeFra.year, overgangsstønad.periodeFra.month), null, emptyList())
        )
        val sumInntekt = overgangsstønadMapPost.sumInntekt
        val inntektPostListe = overgangsstønadMapPost.inntektPostListe.toMutableList()
        inntektPostListe.add(
            InntektPost(
                kode = "Overgangsstønad ${overgangsstønad.periodeFra}",
                visningsnavn = "Overgangsstønad",
                beløp = overgangsstønad.belop.toBigDecimal()
            )
        )
        overgangsstønadMap[key] = SummertÅrsinntekt(
            inntektType = InntektType.OVERGANGSSTØNAD,
            visningsnavn = "", referanse = "", sumInntekt = sumInntekt.add(overgangsstønad.belop.toBigDecimal()),
            periodeFra = overgangsstønadMapPost.periodeFra, periodeTil = null, inntektPostListe = inntektPostListe
        )
    }


    // Sjekker om inntekt-dato er innenfor antall måneder (angitt som parameter)
    private fun innenforAntallMnd(
        overgangsstonadDto: OvergangsstonadDto,
        periodeFraSisteMottatteOvergangsstønad: YearMonth,
        antallMnd: Int
    ): Boolean {
        val sammenlignMedDato = periodeFraSisteMottatteOvergangsstønad.minusMonths(antallMnd.toLong())
        return sammenlignMedDato.isBefore(YearMonth.of(overgangsstonadDto.periodeFra.year, overgangsstonadDto.periodeFra.month))
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }

    companion object {
        const val KEY_3MND = "3MND"
        const val KEY_12MND = "12MND"
    }

}






