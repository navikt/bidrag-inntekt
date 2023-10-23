package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektRapportering
import no.nav.bidrag.domain.tid.FomMåned
import no.nav.bidrag.domain.tid.TomMåned
import no.nav.bidrag.transport.behandling.inntekt.request.Kontantstotte
import no.nav.bidrag.transport.behandling.inntekt.response.SummertAarsinntekt
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
@Suppress("NonAsciiCharacters")
class KontantstøtteService() {

    // Summerer mottat periode opp til beløp for 12 måneder og returnerer
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

    // Summerer kontantstøtte for angitt barn
    fun beregnKontantstøttePerBarn(kontantstøtteListePerBarn: List<Kontantstotte>): List<SummertAarsinntekt> {
        return if (kontantstøtteListePerBarn.isNotEmpty()) {
            val kontantstøtteListeUt = mutableListOf<SummertAarsinntekt>()
            val barnPersonId = kontantstøtteListePerBarn.first().barnPersonId

            kontantstøtteListePerBarn.forEach {
                kontantstøtteListeUt.add(
                    SummertAarsinntekt(
                        inntektRapportering = InntektRapportering.KONTANTSTØTTE,
                        visningsnavn = InntektRapportering.KONTANTSTØTTE.visningsnavn,
                        referanse = "",
                        sumInntekt = it.belop.times(BigDecimal.valueOf(12)),
                        periodeFra = FomMåned(YearMonth.of(it.periodeFra.year, it.periodeFra.month)),
                        periodeTom = if (it.periodeTil == null) {
                            null
                        } else {
                            it.periodeTil!!.minusMonths(1).let {
                                TomMåned(YearMonth.of(it.year, it.month))
                            }
                        },
                        gjelderBarnPersonId = barnPersonId,
                        inntektPostListe = emptyList()
                    )
                )
            }
            kontantstøtteListeUt.sortedWith(compareBy({ it.inntektRapportering.toString() }, { it.periodeFra }))
        } else {
            emptyList()
        }
    }
}
