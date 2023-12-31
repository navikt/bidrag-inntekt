package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.domene.util.visningsnavn
import no.nav.bidrag.inntekt.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.YearMonth

@DisplayName("UtvidetBarnetrygdOgSmåbarnstilleggServiceTest")
class UtvidetBarnetrygdOgSmåbarnstilleggServiceTest : AbstractServiceTest() {

    @Test
    fun `skal returnere summert årsbeløp for hhv utvidet barnetrygd og småbarnstillegg`() {
        val utvidetBarnetrygdOgSmåbarnstilleggService = UtvidetBarnetrygdOgSmåbarnstilleggService()

        val ubst = TestUtil.byggUtvidetBarnetrygdOgSmåbarnstillegg()
        val beregnetUtvidetBarnetrygdOgSmåbarnstillegg = utvidetBarnetrygdOgSmåbarnstilleggService.beregnUtvidetBarnetrygdOgSmåbarnstillegg(ubst)

        assertAll(
            Executable { assertNotNull(beregnetUtvidetBarnetrygdOgSmåbarnstillegg) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg.size).isEqualTo(5) },

            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].inntektRapportering,
                ).isEqualTo(Inntektsrapportering.SMÅBARNSTILLEGG)
            },
            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].visningsnavn,
                ).isEqualTo(Inntektsrapportering.SMÅBARNSTILLEGG.visningsnavn.intern)
            },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].sumInntekt).isEqualTo(BigDecimal.valueOf(7920)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].periode.fom).isEqualTo(YearMonth.parse("2021-11")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].periode.til).isEqualTo(YearMonth.parse("2022-03")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].inntektPostListe.size).isEqualTo(0) },

            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].inntektRapportering,
                ).isEqualTo(Inntektsrapportering.SMÅBARNSTILLEGG)
            },
            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].visningsnavn,
                ).isEqualTo(Inntektsrapportering.SMÅBARNSTILLEGG.visningsnavn.intern)
            },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].sumInntekt).isEqualTo(BigDecimal.valueOf(7920)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].periode.fom).isEqualTo(YearMonth.parse("2022-06")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].periode.til).isEqualTo(YearMonth.parse("2022-07")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].inntektPostListe.size).isEqualTo(0) },

            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].inntektRapportering,
                ).isEqualTo(Inntektsrapportering.SMÅBARNSTILLEGG)
            },
            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].visningsnavn,
                ).isEqualTo(Inntektsrapportering.SMÅBARNSTILLEGG.visningsnavn.intern)
            },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].sumInntekt).isEqualTo(BigDecimal.valueOf(7920)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].periode.fom).isEqualTo(YearMonth.parse("2022-10")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].periode.til).isNull() },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].inntektPostListe.size).isEqualTo(0) },

            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].inntektRapportering,
                ).isEqualTo(Inntektsrapportering.UTVIDET_BARNETRYGD)
            },
            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].visningsnavn,
                ).isEqualTo(Inntektsrapportering.UTVIDET_BARNETRYGD.visningsnavn.intern)
            },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].sumInntekt).isEqualTo(BigDecimal.valueOf(12648)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].periode.fom).isEqualTo(YearMonth.parse("2019-01")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].periode.til).isEqualTo(YearMonth.parse("2019-09")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].inntektPostListe.size).isEqualTo(0) },

            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].inntektRapportering,
                ).isEqualTo(Inntektsrapportering.UTVIDET_BARNETRYGD)
            },
            Executable {
                assertThat(
                    beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].visningsnavn,
                ).isEqualTo(Inntektsrapportering.UTVIDET_BARNETRYGD.visningsnavn.intern)
            },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].sumInntekt).isEqualTo(BigDecimal.valueOf(12648)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].periode.fom).isEqualTo(YearMonth.parse("2020-11")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].periode.til).isEqualTo(YearMonth.parse("2022-09")) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].inntektPostListe.size).isEqualTo(0) },

        )
    }
}
