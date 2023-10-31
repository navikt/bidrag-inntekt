package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektRapportering
import no.nav.bidrag.domain.tid.FomMåned
import no.nav.bidrag.domain.tid.TomMåned
import no.nav.bidrag.transport.behandling.inntekt.request.UtvidetBarnetrygdOgSmåbarnstillegg
import no.nav.bidrag.transport.behandling.inntekt.response.SummertÅrsinntekt
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
@Suppress("NonAsciiCharacters")
class UtvidetBarnetrygdOgSmåbarnstilleggService() {

    // Summerer, grupperer og transformerer utvidet barnetrygd og småbarnstillegg pr år
    fun beregnUtvidetBarnetrygdOgSmåbarnstillegg(utvidetBarnetrygdOgSmåbarnstillegglisteInn: List<UtvidetBarnetrygdOgSmåbarnstillegg>): List<SummertÅrsinntekt> {
        return if (utvidetBarnetrygdOgSmåbarnstillegglisteInn.isNotEmpty()) {
            val utvidetBarnetrygdListe = utvidetBarnetrygdOgSmåbarnstillegglisteInn.filter { it.type == "UTVIDET" }
            val småbarnstilleggListe = utvidetBarnetrygdOgSmåbarnstillegglisteInn.filter { it.type == "SMÅBARNSTILLEGG" }

            val utvidetBarnetrygdOgSmåbarnstilleggListeUt = mutableListOf<SummertÅrsinntekt>()

            utvidetBarnetrygdListe.forEach {
                utvidetBarnetrygdOgSmåbarnstilleggListeUt.add(
                    SummertÅrsinntekt(
                        inntektRapportering = InntektRapportering.UTVIDET_BARNETRYGD,
                        visningsnavn = InntektRapportering.UTVIDET_BARNETRYGD.visningsnavn,
                        referanse = "",
                        sumInntekt = it.beløp.times(BigDecimal.valueOf(12)),
                        periodeFra = FomMåned(YearMonth.of(it.periodeFra.year, it.periodeFra.month)),
                        periodeTom = if (it.periodeTil == null) {
                            null
                        } else {
                            it.periodeTil!!.minusMonths(1).let {
                                TomMåned(YearMonth.of(it.year, it.month))
                            }
                        },
                        inntektPostListe = emptyList()
                    )
                )
            }
            småbarnstilleggListe.forEach {
                utvidetBarnetrygdOgSmåbarnstilleggListeUt.add(
                    SummertÅrsinntekt(
                        inntektRapportering = InntektRapportering.SMÅBARNSTILLEGG,
                        visningsnavn = InntektRapportering.SMÅBARNSTILLEGG.visningsnavn,
                        referanse = "",
                        sumInntekt = it.beløp.times(BigDecimal.valueOf(12)),
                        periodeFra = FomMåned(YearMonth.of(it.periodeFra.year, it.periodeFra.month)),
                        periodeTom = if (it.periodeTil == null) {
                            null
                        } else {
                            it.periodeTil!!.minusMonths(1).let {
                                TomMåned(YearMonth.of(it.year, it.month))
                            }
                        },
                        inntektPostListe = emptyList()
                    )
                )
            }
            utvidetBarnetrygdOgSmåbarnstilleggListeUt.sortedWith(compareBy({ it.inntektRapportering.toString() }, { it.periodeFra }))
        } else {
            emptyList()
        }
    }
}
