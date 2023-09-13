package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.CUT_OFF_DATO
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.KEY_12MND
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.KEY_3MND
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.finnAntallMndOverlapp
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.finnSisteAarSomSkalRapporteres
import no.nav.bidrag.inntekt.util.beregneBeløpPerMåned
import no.nav.bidrag.inntekt.util.isNumeric
import no.nav.bidrag.transport.behandling.grunnlag.response.OvergangsstonadDto
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.SummertAarsinntekt
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Service
@Suppress("NonAsciiCharacters")
class OvergangsstønadService(private val dateProvider: DateProvider) {

    // Summerer, grupperer og transformerer overgangsstønader pr år
    fun beregnOvergangsstønad(overgangsstønadListeInn: List<OvergangsstonadDto>): List<SummertAarsinntekt> {
        return if (overgangsstønadListeInn.isNotEmpty()) {
            val overgangsstønadMap = summerAarsinntekter(overgangsstønadListeInn)
            val overgangsstønadListeUt = mutableListOf<SummertAarsinntekt>()

            overgangsstønadMap.forEach {
                if (it.key.isNumeric() && it.key.toInt() > finnSisteAarSomSkalRapporteres(dateProvider.getCurrentDate())) {
                    return@forEach // Går videre til neste forekomst
                }
                overgangsstønadListeUt.add(
                    SummertAarsinntekt(
                        inntektBeskrivelse = when (it.key) {
                            KEY_3MND -> InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_3MND
                            KEY_12MND -> InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_12MND
                            else -> InntektBeskrivelse.OVERGANGSSTØNAD
                        },
                        visningsnavn = when (it.key) {
                            KEY_3MND -> InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_3MND.visningsnavn
                            KEY_12MND -> InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_12MND.visningsnavn
                            else -> "${InntektBeskrivelse.OVERGANGSSTØNAD.visningsnavn} ${it.value.periodeFra.year}"
                        },
                        referanse = "",
                        sumInntekt = when (it.key) {
                            KEY_3MND -> it.value.sumInntekt.toInt().times(4).toBigDecimal() // Regner om til årsinntekt
                            else -> it.value.sumInntekt
                        },
                        periodeFra = it.value.periodeFra,
                        periodeTil = it.value.periodeTil,
                        inntektPostListe = it.value.inntektPostListe
                    )
                )
            }
            overgangsstønadListeUt.sortedWith(compareBy({ it.inntektBeskrivelse.toString() }, { it.periodeFra }))
        } else {
            emptyList()
        }
    }

    // Summerer og grupperer overgangsstønader pr år
    private fun summerAarsinntekter(overgangsstønadListeInn: List<OvergangsstonadDto>): Map<String, InntektSumPost> {
        val overgangsstønadMap = mutableMapOf<String, InntektSumPost>()
        overgangsstønadListeInn.forEach { overgangsstønad ->
            kalkulerBelopForPeriode(
                overgangsstønad.periodeFra,
                overgangsstønad.periodeTil,
                overgangsstønad.belop
            ).forEach { periodeMap ->
                akkumulerPost(overgangsstønadMap, periodeMap.key, periodeMap.value)
            }
        }

        if (!overgangsstønadMap.containsKey(KEY_3MND)) {
            val periode = bestemPeriode(KEY_3MND)
            overgangsstønadMap[KEY_3MND] = InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
        }
        if (!overgangsstønadMap.containsKey(KEY_12MND)) {
            val periode = bestemPeriode(KEY_12MND)
            overgangsstønadMap[KEY_12MND] = InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
        }

        return overgangsstønadMap.toMap()
    }

    // Summerer inntekter og legger til detaljposter til map
    private fun akkumulerPost(overgangsstonadMap: MutableMap<String, InntektSumPost>, key: String, value: Int) {
        val periode = bestemPeriode(key)
        val inntektSumPost =
            overgangsstonadMap.getOrDefault(key, InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf()))
        val sumInntekt = inntektSumPost.sumInntekt
        val inntektPostListe = inntektSumPost.inntektPostListe
        val nyInntektPost = InntektPost("overgangsstønad", "Overgangsstønad", value.toBigDecimal())
        inntektPostListe.add(nyInntektPost)
        overgangsstonadMap[key] = InntektSumPost(sumInntekt.add(value.toBigDecimal()), periode.periodeFra, periode.periodeTil, inntektPostListe)
    }

    // Kalkulerer beløp for periode (år eller intervall)
    private fun kalkulerBelopForPeriode(periodeFra: LocalDate, periodeTil: LocalDate?, belop: Int): Map<String, Int> {
        val periodeFraYM = YearMonth.of(periodeFra.year, periodeFra.month)
        val periodeTilYM = if (periodeTil != null) {
            YearMonth.of(periodeTil.year, periodeTil.month)
        } else {
            YearMonth.of(periodeFraYM.year, periodeFraYM.month).plusMonths(1)
        }

        // Returner map med en forekomst for hvert år beløpet dekker + forekomst for siste 3 mnd + forekomst for siste 12 mnd
        return kalkulerBelopForÅr(periodeFraYM, periodeTilYM, belop) +
            kalkulerBeløpForIntervall(periodeFraYM, periodeTilYM, belop, KEY_3MND) +
            kalkulerBeløpForIntervall(periodeFraYM, periodeTilYM, belop, KEY_12MND)
    }

    // Kalkulerer totalt beløp for hvert år forekomsten dekker
    private fun kalkulerBelopForÅr(periodeFra: YearMonth, periodeTil: YearMonth, beløp: Int): Map<String, Int> {
        val periodeMap = mutableMapOf<String, Int>()
        val antallMndTotalt = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val månedsbeløp = beregneBeløpPerMåned(beløp, antallMndTotalt)
        val forsteÅr = periodeFra.year
        val sisteÅr = periodeTil.minusMonths(1).year

        for (år in forsteÅr..sisteÅr) {
            val antallMndIÅr = when {
                periodeFra.year == år && periodeTil.year == år -> periodeTil.monthValue.minus(periodeFra.monthValue)
                periodeFra.year == år -> 13.minus(periodeFra.monthValue)
                periodeTil.year == år -> periodeTil.monthValue.minus(1)
                else -> 12
            }
            if (antallMndIÅr > 0) {
                periodeMap[år.toString()] = antallMndIÅr.times(månedsbeløp)
            }
        }

        return periodeMap
    }

    // Kalkulerer totalt beløp for intervall (3 mnd eller 12 mnd) som forekomsten evt dekker
    private fun kalkulerBeløpForIntervall(
        periodeFra: YearMonth,
        periodeTil: YearMonth,
        beløp: Int,
        beregningsperiode: String
    ): Map<String, Int> {
        val periodeMap = mutableMapOf<String, Int>()
        val antallMndTotalt = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val maanedsbeløp = beregneBeløpPerMåned(beløp, antallMndTotalt)

        // TODO Bør CUT_OFF_DATO være dynamisk? (se https://www.skatteetaten.no/bedrift-og-organisasjon/arbeidsgiver/a-meldingen/frister-og-betaling-i-a-meldingen/)
        val sistePeriodeIIntervall = if (dateProvider.getCurrentDate().dayOfMonth > CUT_OFF_DATO) {
            YearMonth.of(dateProvider.getCurrentDate().year, dateProvider.getCurrentDate().month)
        } else {
            YearMonth.of(dateProvider.getCurrentDate().year, dateProvider.getCurrentDate().month).minusMonths(1)
        }
        val forstePeriodeIIntervall =
            if (beregningsperiode == KEY_3MND) sistePeriodeIIntervall.minusMonths(3) else sistePeriodeIIntervall.minusMonths(12)

        val antallMndOverlapp = finnAntallMndOverlapp(periodeFra, periodeTil, forstePeriodeIIntervall, sistePeriodeIIntervall)

        if (antallMndOverlapp > 0) {
            periodeMap[beregningsperiode] = antallMndOverlapp.times(maanedsbeløp)
        }

        return periodeMap
    }

    // Finner riktig periode basert på nøkkelverdi i map og om det er type år, måned eller intervall
    private fun bestemPeriode(periodeVerdi: String): Periode {
        val periodeFra: YearMonth
        val periodeTil: YearMonth

        // År
        if (periodeVerdi.isNumeric() && periodeVerdi.length == 4) {
            periodeFra = YearMonth.of(periodeVerdi.toInt(), 1)
            periodeTil = YearMonth.of(periodeVerdi.toInt(), 12)
            // Intervall
        } else {
            // TODO Bør CUT_OFF_DATO være dynamisk? (se https://www.skatteetaten.no/bedrift-og-organisasjon/arbeidsgiver/a-meldingen/frister-og-betaling-i-a-meldingen/)
            periodeTil = if (dateProvider.getCurrentDate().dayOfMonth > CUT_OFF_DATO) {
                YearMonth.of(dateProvider.getCurrentDate().year, dateProvider.getCurrentDate().month).minusMonths(1)
            } else {
                YearMonth.of(dateProvider.getCurrentDate().year, dateProvider.getCurrentDate().month).minusMonths(2)
            }
            periodeFra = if (periodeVerdi == KEY_3MND) periodeTil.minusMonths(2) else periodeTil.minusMonths(11)
        }

        return Periode(periodeFra, periodeTil)
    }
}
