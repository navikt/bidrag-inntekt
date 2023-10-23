package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektRapportering
import no.nav.bidrag.domain.tid.FomMåned
import no.nav.bidrag.domain.tid.TomMåned
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.YearMonth

@Suppress("NonAsciiCharacters")
@DisplayName("UtvidetBarnetrygdOgSmåbarnstilleggServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class UtvidetBarnetrygdOgSmåbarnstilleggServiceTest {

    @Test
    fun `skal returnere summert årsbeløp for hhv utvidet barnetrygd og småbarnstillegg`() {
        val utvidetBarnetrygdOgSmåbarnstilleggService = UtvidetBarnetrygdOgSmåbarnstilleggService()

        val ubst = TestUtil.byggUtvidetBarnetrygdOgSmåbarnstillegg()
        val beregnetUtvidetBarnetrygdOgSmåbarnstillegg = utvidetBarnetrygdOgSmåbarnstilleggService.beregnUtvidetBarnetrygdOgSmåbarnstillegg(ubst)

        assertAll(
            Executable { assertNotNull(beregnetUtvidetBarnetrygdOgSmåbarnstillegg) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg.size).isEqualTo(5) },

            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].inntektRapportering).isEqualTo(InntektRapportering.SMÅBARNSTILLEGG) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].visningsnavn).isEqualTo(InntektRapportering.SMÅBARNSTILLEGG.visningsnavn) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].sumInntekt).isEqualTo(BigDecimal.valueOf(7920)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-11"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-03"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[0].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].inntektRapportering).isEqualTo(InntektRapportering.SMÅBARNSTILLEGG) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].visningsnavn).isEqualTo(InntektRapportering.SMÅBARNSTILLEGG.visningsnavn) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].sumInntekt).isEqualTo(BigDecimal.valueOf(7920)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-06"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-07"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[1].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].inntektRapportering).isEqualTo(InntektRapportering.SMÅBARNSTILLEGG) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].visningsnavn).isEqualTo(InntektRapportering.SMÅBARNSTILLEGG.visningsnavn) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].sumInntekt).isEqualTo(BigDecimal.valueOf(7920)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-10"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].periodeTom).isNull() },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[2].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].inntektRapportering).isEqualTo(InntektRapportering.UTVIDET_BARNETRYGD) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].visningsnavn).isEqualTo(InntektRapportering.UTVIDET_BARNETRYGD.visningsnavn) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].sumInntekt).isEqualTo(BigDecimal.valueOf(12648)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2019-01"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2019-09"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[3].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].inntektRapportering).isEqualTo(InntektRapportering.UTVIDET_BARNETRYGD) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].visningsnavn).isEqualTo(InntektRapportering.UTVIDET_BARNETRYGD.visningsnavn) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].sumInntekt).isEqualTo(BigDecimal.valueOf(12648)) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2020-11"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-09"))) },
            Executable { assertThat(beregnetUtvidetBarnetrygdOgSmåbarnstillegg[4].inntektPostListe.size).isEqualTo(0) }

        )
    }
}
