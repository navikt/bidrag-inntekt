package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.dto.InntektPost
import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.SummertAarsinntekt
import no.nav.bidrag.inntekt.dto.SummertMaanedsinntekt
import no.nav.bidrag.transport.behandling.grunnlag.response.AinntektDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Service
class AinntektService {

    // Summerer, grupperer og transformerer ainntekter pr år
    fun beregnAarsinntekt(ainntektListeInn: List<AinntektDto>): List<SummertAarsinntekt> {
        val ainntektMap = summerAarsinntekter(ainntektListeInn)
        val ainntektListeUt = mutableListOf<SummertAarsinntekt>()

        ainntektMap.forEach {
            ainntektListeUt.add(
                SummertAarsinntekt(
                    inntektType = when (it.key) {
                        KEY_3MND -> InntektType.AINNTEKT_BEREGNET_3MND
                        KEY_12MND -> InntektType.AINNTEKT_BEREGNET_12MND
                        else -> InntektType.AINNTEKT
                    },
                    visningsnavn = "",
                    referanse = "",
                    sumInntekt = it.value.sumInntekt,
                    periodeFra = it.value.periodeFra,
                    periodeTil = it.value.periodeTil,
                    inntektPostListe = grupperOgSummerDetaljposter(it.value.inntektPostListe)
                )
            )
        }

        return ainntektListeUt.sortedWith(compareBy({ it.inntektType.toString() }, { it.periodeFra }))
    }

    // Summerer, grupperer og transformerer ainntekter pr måned
    fun beregnMaanedsinntekt(ainntektListeInn: List<AinntektDto>): List<SummertMaanedsinntekt> {
        val ainntektMap = summerMaanedsinntekter(ainntektListeInn)
        val ainntektListeUt = mutableListOf<SummertMaanedsinntekt>()

        ainntektMap.forEach {
            ainntektListeUt.add(
                SummertMaanedsinntekt(
                    periode = YearMonth.of(it.key.substring(0, 4).toInt(), it.key.substring(4, 6).toInt()),
                    sumInntekt = it.value.sumInntekt,
                    inntektPostListe = grupperOgSummerDetaljposter(it.value.inntektPostListe)
                )
            )
        }

        return ainntektListeUt.sortedWith(compareBy { it.periode })
    }

    // Grupperer og summer poster som har samme kode/beskrivelse
    private fun grupperOgSummerDetaljposter(inntektPostListe: List<InntektPost>): List<InntektPost> {
        return inntektPostListe
            .groupBy(InntektPost::kode)
            .map { InntektPost(it.key, "", it.value.sumOf(InntektPost::beløp)) }
    }

    // Summerer og grupperer ainntekter pr år
    private fun summerAarsinntekter(ainntektListeInn: List<AinntektDto>): Map<String, InntektSumPost> {
        val ainntektMap = mutableMapOf<String, InntektSumPost>()
        ainntektListeInn.forEach { ainntekt ->
            ainntekt.ainntektspostListe.forEach { ainntektPost ->
                kalkulerBelopForPeriode(
                    ainntektPost.opptjeningsperiodeFra,
                    ainntektPost.opptjeningsperiodeTil,
                    ainntektPost.utbetalingsperiode!!,
                    ainntektPost.beskrivelse!!,
                    ainntektPost.belop.intValueExact(),
                    PERIODE_AAR
                ).forEach { periodeMap ->
                    akkumulerPost(ainntektMap, periodeMap.key, periodeMap.value)
                }
            }
        }
        return ainntektMap.toMap()
    }

    // Summerer og grupperer ainntekter pr måned
    private fun summerMaanedsinntekter(ainntektListeInn: List<AinntektDto>): Map<String, InntektSumPost> {
        val ainntektMap = mutableMapOf<String, InntektSumPost>()
        ainntektListeInn.forEach { ainntekt ->
            ainntekt.ainntektspostListe.forEach { ainntektPost ->
                kalkulerBelopForPeriode(
                    ainntektPost.opptjeningsperiodeFra,
                    ainntektPost.opptjeningsperiodeTil,
                    ainntektPost.utbetalingsperiode!!,
                    ainntektPost.beskrivelse!!,
                    ainntektPost.belop.intValueExact(),
                    PERIODE_MAANED
                ).forEach { periodeMap ->
                    akkumulerPost(ainntektMap, periodeMap.key, periodeMap.value)
                }
            }
        }
        return ainntektMap.toMap()
    }

    // Summerer inntekter og legger til detaljposter til map
    private fun akkumulerPost(ainntektMap: MutableMap<String, InntektSumPost>, key: String, value: Detaljpost) {
        val periode = bestemPeriode(key)
        val inntektSumPost = ainntektMap.getOrDefault(key, InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf()))
        val sumInntekt = inntektSumPost.sumInntekt
        val inntektPostListe = inntektSumPost.inntektPostListe
        val nyInntektPost = InntektPost(value.kode, "", value.belop.toBigDecimal())
        inntektPostListe.add(nyInntektPost)
        ainntektMap[key] = InntektSumPost(sumInntekt.add(value.belop.toBigDecimal()), periode.periodeFra, periode.periodeTil, inntektPostListe)
    }

    // Kalkulerer beløp for periode
    private fun kalkulerBelopForPeriode(
        opptjeningsperiodeFra: LocalDate?,
        opptjeningsperiodeTil: LocalDate?,
        utbetalingsperiode: String,
        beskrivelse: String,
        belop: Int,
        beregningsperiode: String
    ): Map<String, Detaljpost> {
        val periodeFra: YearMonth
        val periodeTil: YearMonth

        if (opptjeningsperiodeFra != null) {
            periodeFra = YearMonth.of(opptjeningsperiodeFra.year, opptjeningsperiodeFra.month)
        } else {
            periodeFra = YearMonth.of(utbetalingsperiode.substring(0, 4).toInt(), utbetalingsperiode.substring(5, 7).toInt())
        }

        if (opptjeningsperiodeTil != null) {
            periodeTil = YearMonth.of(opptjeningsperiodeTil.year, opptjeningsperiodeTil.month)
        } else {
            if (opptjeningsperiodeFra != null) {
                periodeTil = YearMonth.of(opptjeningsperiodeFra.year, opptjeningsperiodeFra.month).plusMonths(1)
            } else {
                periodeTil = YearMonth.of(utbetalingsperiode.substring(0, 4).toInt(), utbetalingsperiode.substring(5, 7).toInt()).plusMonths(1)
            }
        }

        return when (beregningsperiode) {
            PERIODE_MAANED -> kalkulerBelopForMnd(periodeFra, periodeTil, beskrivelse, belop)
            else -> kalkulerBelopForAar(periodeFra, periodeTil, beskrivelse, belop) +
                kalkulerBelopForIntervall(periodeFra, periodeTil, beskrivelse, belop, KEY_3MND) +
                kalkulerBelopForIntervall(periodeFra, periodeTil, beskrivelse, belop, KEY_12MND)
        }
    }

    private fun kalkulerBelopForMnd(periodeFra: YearMonth, periodeTil: YearMonth, beskrivelse: String, belop: Int): Map<String, Detaljpost> {
        val periodeMap = mutableMapOf<String, Detaljpost>()
        val antallMnd = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val månedsbeløp = belop.div(antallMnd)
        var periode = periodeFra

        while (periode.isBefore(periodeTil)) {
            periodeMap[periode.year.toString() + periode.toString().substring(5, 7)] = Detaljpost(månedsbeløp, beskrivelse)
            periode = periode.plusMonths(1)
        }

        return periodeMap
    }

    private fun kalkulerBelopForAar(periodeFra: YearMonth, periodeTil: YearMonth, beskrivelse: String, belop: Int): Map<String, Detaljpost> {
        val periodeMap = mutableMapOf<String, Detaljpost>()
        val antallMndTotalt = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val maanedsbelop = belop.div(antallMndTotalt)
        val forsteAar = periodeFra.year
        val sisteAar = periodeTil.minusMonths(1).year

        for (aar in forsteAar..sisteAar) {
            val antallMndIAar = when {
                periodeFra.year == aar && periodeTil.year == aar -> periodeTil.monthValue.minus(periodeFra.monthValue)
                periodeFra.year == aar -> 13.minus(periodeFra.monthValue)
                periodeTil.year == aar -> periodeTil.monthValue.minus(1)
                else -> 12
            }
            if (antallMndIAar > 0) {
                periodeMap[aar.toString()] = Detaljpost(antallMndIAar.times(maanedsbelop), beskrivelse)
            }
        }

        return periodeMap
    }

    private fun kalkulerBelopForIntervall(
        periodeFra: YearMonth,
        periodeTil: YearMonth,
        beskrivelse: String,
        belop: Int,
        beregningsperiode: String
    ): Map<String, Detaljpost> {
        val periodeMap = mutableMapOf<String, Detaljpost>()
        val antallMndTotalt = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val maanedsbelop = belop.div(antallMndTotalt)
        val dagensDato = LocalDate.now()

        // TODO Bør CUT_OFF_DATO være dynamisk? (se https://www.skatteetaten.no/bedrift-og-organisasjon/arbeidsgiver/a-meldingen/frister-og-betaling-i-a-meldingen/)
        val sistePeriodeIIntervall = if (dagensDato.dayOfMonth > CUT_OFF_DATO) {
            YearMonth.of(dagensDato.year, dagensDato.month)
        } else {
            YearMonth.of(dagensDato.year, dagensDato.month).minusMonths(1)
        }
        val forstePeriodeIIntervall =
            if (beregningsperiode == KEY_3MND) sistePeriodeIIntervall.minusMonths(3) else sistePeriodeIIntervall.minusMonths(12)

        val antallMndOverlapp = when {
            !(periodeTil.isAfter(forstePeriodeIIntervall)) -> 0
            !(periodeFra.isBefore(sistePeriodeIIntervall)) -> 0
            !(periodeFra.isAfter(forstePeriodeIIntervall)) && !(periodeTil.isBefore(sistePeriodeIIntervall)) ->
                ChronoUnit.MONTHS.between(forstePeriodeIIntervall, sistePeriodeIIntervall).toInt()

            !(periodeFra.isAfter(forstePeriodeIIntervall)) && (periodeTil.isBefore(sistePeriodeIIntervall)) ->
                ChronoUnit.MONTHS.between(forstePeriodeIIntervall, periodeTil).toInt()

            (periodeFra.isAfter(forstePeriodeIIntervall)) && !(periodeTil.isBefore(sistePeriodeIIntervall)) ->
                ChronoUnit.MONTHS.between(periodeFra, sistePeriodeIIntervall).toInt()

            else -> ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        }

        if (antallMndOverlapp > 0) {
            periodeMap[beregningsperiode] = Detaljpost(antallMndOverlapp.times(maanedsbelop), beskrivelse)
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
            // Måned
        } else if (periodeVerdi.isNumeric() && periodeVerdi.length == 6) {
            periodeFra = YearMonth.of(periodeVerdi.substring(0, 4).toInt(), periodeVerdi.substring(4, 6).toInt())
            periodeTil = periodeFra
            // Intervall
        } else {
            val dagensDato = LocalDate.now()
            // TODO Bør CUT_OFF_DATO være dynamisk? (se https://www.skatteetaten.no/bedrift-og-organisasjon/arbeidsgiver/a-meldingen/frister-og-betaling-i-a-meldingen/)
            periodeTil = if (dagensDato.dayOfMonth > CUT_OFF_DATO) {
                YearMonth.of(dagensDato.year, dagensDato.month).minusMonths(1)
            } else {
                YearMonth.of(dagensDato.year, dagensDato.month).minusMonths(2)
            }
            periodeFra = if (periodeVerdi == KEY_3MND) periodeTil.minusMonths(2) else periodeTil.minusMonths(11)
        }

        return Periode(periodeFra, periodeTil)
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }

    companion object {
        const val KEY_3MND = "3MND"
        const val KEY_12MND = "12MND"
        const val PERIODE_AAR = "AAR"
        const val PERIODE_MAANED = "MND"
        const val CUT_OFF_DATO = 10
    }

    data class Detaljpost(
        val belop: Int,
        val kode: String
    )
}
