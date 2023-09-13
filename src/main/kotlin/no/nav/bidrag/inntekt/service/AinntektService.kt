package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.CUT_OFF_DATO
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.KEY_12MND
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.KEY_3MND
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.PERIODE_AAR
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.PERIODE_MAANED
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.finnAntallMndOverlapp
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.finnSisteAarSomSkalRapporteres
import no.nav.bidrag.inntekt.util.isNumeric
import no.nav.bidrag.transport.behandling.grunnlag.response.AinntektDto
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.SummertAarsinntekt
import no.nav.bidrag.transport.behandling.inntekt.response.SummertMaanedsinntekt
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Service
class AinntektService(private val dateProvider: DateProvider) {

    // Summerer, grupperer og transformerer ainntekter pr år
    fun beregnAarsinntekt(ainntektListeInn: List<AinntektDto>, kodeverksverdier: GetKodeverkKoderBetydningerResponse?): List<SummertAarsinntekt> {
        return if (ainntektListeInn.isNotEmpty()) {
            val ainntektMap = summerAarsinntekter(ainntektListeInn)
            val ainntektListeUt = mutableListOf<SummertAarsinntekt>()

            ainntektMap.forEach {
                if (it.key.isNumeric() && it.key.toInt() > finnSisteAarSomSkalRapporteres(dateProvider.getCurrentDate())) {
                    return@forEach // Går videre til neste forekomst
                }
                ainntektListeUt.add(
                    SummertAarsinntekt(
                        inntektBeskrivelse = when (it.key) {
                            KEY_3MND -> InntektBeskrivelse.AINNTEKT_BEREGNET_3MND
                            KEY_12MND -> InntektBeskrivelse.AINNTEKT_BEREGNET_12MND
                            else -> InntektBeskrivelse.AINNTEKT
                        },
                        visningsnavn = when (it.key) {
                            KEY_3MND -> InntektBeskrivelse.AINNTEKT_BEREGNET_3MND.visningsnavn
                            KEY_12MND -> InntektBeskrivelse.AINNTEKT_BEREGNET_12MND.visningsnavn
                            else -> "${InntektBeskrivelse.AINNTEKT.visningsnavn} ${it.value.periodeFra.year}"
                        },
                        referanse = "",
                        sumInntekt = when (it.key) {
                            KEY_3MND -> it.value.sumInntekt.toInt().times(4).toBigDecimal() // Regner om til årsinntekt
                            else -> it.value.sumInntekt
                        },
                        periodeFra = it.value.periodeFra,
                        periodeTil = it.value.periodeTil,
                        inntektPostListe = when (it.key) {
                            KEY_3MND -> grupperOgSummerDetaljposter(it.value.inntektPostListe, kodeverksverdier, 4)
                            else -> grupperOgSummerDetaljposter(it.value.inntektPostListe, kodeverksverdier)
                        }

                    )
                )
            }
            ainntektListeUt.sortedWith(compareBy({ it.inntektBeskrivelse.toString() }, { it.periodeFra }))
        } else {
            emptyList()
        }
    }

    // Summerer, grupperer og transformerer ainntekter pr måned
    fun beregnMaanedsinntekt(
        ainntektListeInn: List<AinntektDto>,
        kodeverksverdier: GetKodeverkKoderBetydningerResponse?
    ): List<SummertMaanedsinntekt> {
        val ainntektMap = summerMaanedsinntekter(ainntektListeInn)
        val ainntektListeUt = mutableListOf<SummertMaanedsinntekt>()

        ainntektMap.forEach {
            ainntektListeUt.add(
                SummertMaanedsinntekt(
                    periode = YearMonth.of(it.key.substring(0, 4).toInt(), it.key.substring(4, 6).toInt()),
                    sumInntekt = it.value.sumInntekt,
                    inntektPostListe = grupperOgSummerDetaljposter(it.value.inntektPostListe, kodeverksverdier)
                )
            )
        }

        return ainntektListeUt.sortedWith(compareBy { it.periode })
    }

    // Grupperer og summerer poster som har samme kode/beskrivelse
    private fun grupperOgSummerDetaljposter(
        inntektPostListe: List<InntektPost>,
        kodeverksverdier: GetKodeverkKoderBetydningerResponse?,
        multiplikator: Int = 1 // Avviker fra default hvis beløp skal regnes om til årsverdi
    ): List<InntektPost> {
        return inntektPostListe
            .groupBy(InntektPost::kode)
            .map {
                InntektPost(
                    kode = it.key,
                    visningsnavn = if (kodeverksverdier == null) it.key else finnVisningsnavn(it.key, kodeverksverdier),
                    beløp = it.value.sumOf(InntektPost::beløp).toInt().times(multiplikator).toBigDecimal()
                )
            }
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

        if (!ainntektMap.containsKey(KEY_3MND)) {
            val periode = bestemPeriode(KEY_3MND)
            ainntektMap[KEY_3MND] = InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
        }
        if (!ainntektMap.containsKey(KEY_12MND)) {
            val periode = bestemPeriode(KEY_12MND)
            ainntektMap[KEY_12MND] = InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
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

    // Kalkulerer beløp for periode (måned, år eller intervall)
    private fun kalkulerBelopForPeriode(
        opptjeningsperiodeFra: LocalDate?,
        opptjeningsperiodeTil: LocalDate?,
        utbetalingsperiode: String,
        beskrivelse: String,
        belop: Int,
        beregningsperiode: String
    ): Map<String, Detaljpost> {
        val periodeFra = if (opptjeningsperiodeFra != null) {
            YearMonth.of(opptjeningsperiodeFra.year, opptjeningsperiodeFra.month)
        } else {
            YearMonth.of(utbetalingsperiode.substring(0, 4).toInt(), utbetalingsperiode.substring(5, 7).toInt())
        }

        val periodeTil = if (opptjeningsperiodeTil != null) {
            YearMonth.of(opptjeningsperiodeTil.year, opptjeningsperiodeTil.month)
        } else {
            if (opptjeningsperiodeFra != null) {
                YearMonth.of(opptjeningsperiodeFra.year, opptjeningsperiodeFra.month).plusMonths(1)
            } else {
                YearMonth.of(utbetalingsperiode.substring(0, 4).toInt(), utbetalingsperiode.substring(5, 7).toInt()).plusMonths(1)
            }
        }

        // Hvis periode er måned, returner map med en forekomst for hver måned beløpet dekker
        // Hvis periode er år, returner map med en forekomst for hvert år beløpet dekker + forekomst for siste 3 mnd + forekomst for siste 12 mnd
        return when (beregningsperiode) {
            PERIODE_MAANED -> kalkulerBeløpForMnd(periodeFra, periodeTil, beskrivelse, belop)
            else -> kalkulerBelopForAar(periodeFra, periodeTil, beskrivelse, belop) +
                kalkulerBeløpForIntervall(periodeFra, periodeTil, beskrivelse, belop, KEY_3MND) +
                kalkulerBeløpForIntervall(periodeFra, periodeTil, beskrivelse, belop, KEY_12MND)
        }
    }

    private fun kalkulerBeløpForMnd(periodeFra: YearMonth, periodeTil: YearMonth, beskrivelse: String, beløp: Int): Map<String, Detaljpost> {
        val periodeMap = mutableMapOf<String, Detaljpost>()
        val antallMnd = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val månedsbeløp = beregneBeløpPerMåned(beløp, antallMnd)
        var periode = periodeFra

        while (periode.isBefore(periodeTil)) {
            periodeMap[periode.year.toString() + periode.toString().substring(5, 7)] = Detaljpost(månedsbeløp, beskrivelse)
            periode = periode.plusMonths(1)
        }

        return periodeMap
    }

    private fun beregneBeløpPerMåned(beløp: Int, antallMnd: Int): Int {
        return if (antallMnd == 0) {
            0
        } else {
            beløp.div(antallMnd)
        }
    }

    // Kalkulerer totalt beløp for hvert år forekomsten dekker
    private fun kalkulerBelopForAar(periodeFra: YearMonth, periodeTil: YearMonth, beskrivelse: String, beløp: Int): Map<String, Detaljpost> {
        val periodeMap = mutableMapOf<String, Detaljpost>()
        val antallMndTotalt = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val maanedsbeløp = beregneBeløpPerMåned(beløp, antallMndTotalt)
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
                periodeMap[år.toString()] = Detaljpost(antallMndIÅr.times(maanedsbeløp), beskrivelse)
            }
        }

        return periodeMap
    }

    // Kalkulerer totalt beløp for intervall (3 mnd eller 12 mnd) som forekomsten evt dekker
    private fun kalkulerBeløpForIntervall(
        periodeFra: YearMonth,
        periodeTil: YearMonth,
        beskrivelse: String,
        beløp: Int,
        beregningsperiode: String
    ): Map<String, Detaljpost> {
        val periodeMap = mutableMapOf<String, Detaljpost>()
        val antallMndTotalt = ChronoUnit.MONTHS.between(periodeFra, periodeTil).toInt()
        val maanedsbelop = beregneBeløpPerMåned(beløp, antallMndTotalt)

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
//            val dagensDato = LocalDate.now()
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

    private fun finnVisningsnavn(fulltNavnInntektspost: String, kodeverksverdier: GetKodeverkKoderBetydningerResponse): String {
        var visningsnavn = ""
        val bokmål = "nb"
        for ((fulltNavn, betydningListe) in kodeverksverdier.betydninger) {
            if (fulltNavn == fulltNavnInntektspost) {
                for (betydning in betydningListe) {
                    betydning.beskrivelser.let { beskrivelser ->
                        for ((spraak, beskrivelse) in beskrivelser) {
                            if (spraak == bokmål) {
                                visningsnavn = beskrivelse.term
                            }
                        }
                    }
                }
            }
        }
        return if (visningsnavn == "") {
            fulltNavnInntektspost
        } else {
            visningsnavn
        }
    }
}

data class Detaljpost(
    val belop: Int,
    val kode: String
)
