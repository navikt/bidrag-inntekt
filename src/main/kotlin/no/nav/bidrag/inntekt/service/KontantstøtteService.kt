package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektRapportering
import no.nav.bidrag.domain.tid.FomMåned
import no.nav.bidrag.domain.tid.TomMåned
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.CUT_OFF_DATO
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.KEY_12MND
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.KEY_3MND
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.finnAntallMndOverlapp
import no.nav.bidrag.inntekt.util.InntektUtil.Companion.finnSisteAarSomSkalRapporteres
import no.nav.bidrag.inntekt.util.isNumeric
import no.nav.bidrag.transport.behandling.inntekt.request.Kontantstotte
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.SummertAarsinntekt
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

@Service
@Suppress("NonAsciiCharacters")
class KontantstøtteService(private val dateProvider: DateProvider) {

    // Summerer, grupperer og transformerer kontantstøtte pr år per barn
    // Beløp for kontantstøtte er alltid oppgitt som månedsbeløp i vedtaksperiode, ikke totalbeløp for hele perioden
    fun beregnKontantstøtte(kontantstøttelisteInn: List<Kontantstotte>): List<SummertAarsinntekt> {
        val kontantstøtteListeUt = mutableListOf<SummertAarsinntekt>()

        // Lager et sett med unike barnPersonId fra inputliste
        val barnPersonIdListe = kontantstøttelisteInn.distinctBy { it.barnPersonId }.map { it.barnPersonId }.toSet()

        barnPersonIdListe.sortedWith(compareBy { it }).forEach { barnPersonId ->
            val kontantstøtteListePerBarn = kontantstøttelisteInn.filter { it.barnPersonId == barnPersonId }
            beregnKontantstøttePerBarn(kontantstøtteListePerBarn).forEach {
                kontantstøtteListeUt.add(it)
            }
        }
        return kontantstøtteListeUt
    }

    // Summerer, grupperer og transformerer kontantstøtte pr år for angitt barn
    fun beregnKontantstøttePerBarn(kontantstøtteListePerBarn: List<Kontantstotte>): List<SummertAarsinntekt> {
        return if (kontantstøtteListePerBarn.isNotEmpty()) {
            val kontantstøtteMap = summerAarsinntekter(kontantstøtteListePerBarn)
            val kontantstøtteListeUt = mutableListOf<SummertAarsinntekt>()
            val barnPersonId = kontantstøtteListePerBarn.first().barnPersonId

            kontantstøtteMap.forEach {
                if (it.key.isNumeric() && it.key.toInt() > finnSisteAarSomSkalRapporteres(dateProvider.getCurrentDate())) {
                    return@forEach // Går videre til neste forekomst
                }
                kontantstøtteListeUt.add(
                    SummertAarsinntekt(
                        inntektRapportering = when (it.key) {
                            KEY_3MND -> InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND
                            KEY_12MND -> InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND
                            else -> InntektRapportering.KONTANTSTØTTE
                        },
                        visningsnavn = when (it.key) {
                            KEY_3MND -> InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn
                            KEY_12MND -> InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn
                            else -> "${InntektRapportering.KONTANTSTØTTE.visningsnavn} ${it.value.periodeFra.year}"
                        },
                        referanse = "",
                        sumInntekt = when (it.key) {
                            KEY_3MND -> it.value.sumInntekt.toInt().times(4).toBigDecimal() // Regner om til årsinntekt
                            else -> it.value.sumInntekt
                        },
                        periodeFra = FomMåned(it.value.periodeFra),
                        periodeTom = it.value.periodeTil.let { periodeTil -> TomMåned(periodeTil!!) },
                        gjelderBarnPersonId = barnPersonId,
                        inntektPostListe = it.value.inntektPostListe
                    )
                )
            }
            kontantstøtteListeUt.sortedWith(compareBy({ it.inntektRapportering.toString() }, { it.periodeFra }))
        } else {
            emptyList()
        }
    }

    // Summerer og grupperer kontantstøtte pr år
    private fun summerAarsinntekter(kontantstøtteListePerBarn: List<Kontantstotte>): Map<String, InntektSumPost> {
        val kontantstøtteMap = mutableMapOf<String, InntektSumPost>()
        kontantstøtteListePerBarn.forEach { kontantstøtte ->
            kalkulerBelopForPeriode(
                kontantstøtte.periodeFra,
                kontantstøtte.periodeTil,
                kontantstøtte.belop
            ).forEach { periodeMap ->
                akkumulerPost(kontantstøtteMap, periodeMap.key, periodeMap.value)
            }
        }

        if (!kontantstøtteMap.containsKey(KEY_3MND)) {
            val periode = bestemPeriode(KEY_3MND)
            kontantstøtteMap[KEY_3MND] =
                InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
        }
        if (!kontantstøtteMap.containsKey(KEY_12MND)) {
            val periode = bestemPeriode(KEY_12MND)
            kontantstøtteMap[KEY_12MND] =
                InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
        }

        return kontantstøtteMap.toMap()
    }

    // Summerer inntekter og legger til detaljposter til map
    private fun akkumulerPost(kontantstøtteMap: MutableMap<String, InntektSumPost>, key: String, value: BigDecimal) {
        val periode = bestemPeriode(key)
        val inntektSumPost =
            kontantstøtteMap.getOrDefault(
                key,
                InntektSumPost(BigDecimal.ZERO, periode.periodeFra, periode.periodeTil, mutableListOf())
            )
        val sumInntekt = inntektSumPost.sumInntekt
        val inntektPostListe = inntektSumPost.inntektPostListe
        val nyInntektPost = InntektPost("kontantstøtte", "Kontantstøtte", value)
        inntektPostListe.add(nyInntektPost)
        kontantstøtteMap[key] =
            InntektSumPost(sumInntekt.add(value), periode.periodeFra, periode.periodeTil, inntektPostListe)
    }

    // Kalkulerer beløp for periode (år eller intervall)
    private fun kalkulerBelopForPeriode(
        periodeFra: LocalDate,
        periodeTil: LocalDate?,
        belop: BigDecimal
    ): Map<String, BigDecimal> {
        val periodeFraYM = YearMonth.of(periodeFra.year, periodeFra.month)
        val periodeTilYM = if (periodeTil != null) {
            YearMonth.of(periodeTil.year, periodeTil.month)
        } else {
            // Bruker annen logikk for periodeTilYm enn det som ligger for inntekter siden null i periodeTil indikerer at ytelsen er aktiv
            YearMonth.of(LocalDate.now().year, LocalDate.now().month).minusMonths(1)
        }

        // Returner map med en forekomst for hvert år beløpet dekker + forekomst for siste 3 mnd + forekomst for siste 12 mnd
        return kalkulerBeløpForÅr(periodeFraYM, periodeTilYM, belop) +
            kalkulerBeløpForIntervall(periodeFraYM, periodeTilYM, belop, KEY_3MND).toMutableMap() +
            kalkulerBeløpForIntervall(periodeFraYM, periodeTilYM, belop, KEY_12MND)
    }

    // Kalkulerer totalt beløp for hvert år forekomsten dekker
    private fun kalkulerBeløpForÅr(
        periodeFra: YearMonth,
        periodeTil: YearMonth,
        beløp: BigDecimal
    ): Map<String, BigDecimal> {
        val periodeMap = mutableMapOf<String, BigDecimal>()
        val førsteÅr = periodeFra.year
        val sisteÅr = periodeTil.minusMonths(1).year

        for (år in førsteÅr..sisteÅr) {
            val antallMndIÅr = when {
                periodeFra.year == år && periodeTil.year == år -> periodeTil.monthValue.minus(periodeFra.monthValue)
                periodeFra.year == år -> 13.minus(periodeFra.monthValue)
                periodeTil.year == år -> periodeTil.monthValue.minus(1)
                else -> 12
            }
            if (antallMndIÅr > 0) {
                periodeMap[år.toString()] = antallMndIÅr.toBigDecimal().times(beløp)
            }
        }

        return periodeMap
    }

    // Kalkulerer totalt beløp for intervall (3 mnd eller 12 mnd) som forekomsten evt dekker
    private fun kalkulerBeløpForIntervall(
        periodeFra: YearMonth,
        periodeTil: YearMonth,
        beløp: BigDecimal,
        beregningsperiode: String
    ): Map<String, BigDecimal> {
        val periodeMap = mutableMapOf<String, BigDecimal>()

        // TODO Bør CUT_OFF_DATO være dynamisk? (se https://www.skatteetaten.no/bedrift-og-organisasjon/arbeidsgiver/a-meldingen/frister-og-betaling-i-a-meldingen/)
        val sistePeriodeIIntervall = if (dateProvider.getCurrentDate().dayOfMonth > CUT_OFF_DATO) {
            YearMonth.of(dateProvider.getCurrentDate().year, dateProvider.getCurrentDate().month)
        } else {
            YearMonth.of(dateProvider.getCurrentDate().year, dateProvider.getCurrentDate().month).minusMonths(1)
        }
        val forstePeriodeIIntervall =
            if (beregningsperiode == KEY_3MND) {
                sistePeriodeIIntervall.minusMonths(3)
            } else {
                sistePeriodeIIntervall.minusMonths(
                    12
                )
            }

        val antallMndOverlapp =
            finnAntallMndOverlapp(periodeFra, periodeTil, forstePeriodeIIntervall, sistePeriodeIIntervall)

        if (antallMndOverlapp > 0) {
            periodeMap[beregningsperiode] = antallMndOverlapp.toBigDecimal().times(beløp)
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
