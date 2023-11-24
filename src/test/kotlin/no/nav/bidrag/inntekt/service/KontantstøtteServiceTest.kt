package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
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
@DisplayName("KontantstøtteServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@EnableMockOAuth2Server
class KontantstøtteServiceTest {

    @Test
    fun `skal returnere kontantstøtte for alle perioder i input`() {
        val kontantstøtteService = KontantstøtteService()

        val kontantstøtte = TestUtil.byggKontantstøtte()
        val beregnetKontantstøtte = kontantstøtteService.beregnKontantstøtte(kontantstøtte)

        assertAll(
            Executable { assertNotNull(beregnetKontantstøtte) },
            Executable { assertThat(beregnetKontantstøtte.size).isEqualTo(5) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektRapportering).isEqualTo(Inntektsrapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[0].visningsnavn).isEqualTo(Inntektsrapportering.KONTANTSTØTTE.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[0].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[0].periode.fom).isEqualTo(YearMonth.parse("2021-11")) },
            Executable { assertThat(beregnetKontantstøtte[0].periode.til).isEqualTo(YearMonth.parse("2022-06")) },
            Executable { assertThat(beregnetKontantstøtte[0].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetKontantstøtte[1].inntektRapportering).isEqualTo(Inntektsrapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[1].visningsnavn).isEqualTo(Inntektsrapportering.KONTANTSTØTTE.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[1].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[1].periode.fom).isEqualTo(YearMonth.parse("2022-10")) },
            Executable { assertThat(beregnetKontantstøtte[1].periode.til).isEqualTo(YearMonth.parse("2023-01")) },
            Executable { assertThat(beregnetKontantstøtte[1].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetKontantstøtte[2].inntektRapportering).isEqualTo(Inntektsrapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[2].visningsnavn).isEqualTo(Inntektsrapportering.KONTANTSTØTTE.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[2].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[2].periode.fom).isEqualTo(YearMonth.parse("2023-05")) },
            Executable { assertThat(beregnetKontantstøtte[2].periode.til).isEqualTo(YearMonth.parse("2023-07")) },
            Executable { assertThat(beregnetKontantstøtte[2].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetKontantstøtte[3].inntektRapportering).isEqualTo(Inntektsrapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[3].visningsnavn).isEqualTo(Inntektsrapportering.KONTANTSTØTTE.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[3].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[3].periode.fom).isEqualTo(YearMonth.parse("2022-09")) },
            Executable { assertThat(beregnetKontantstøtte[3].periode.til).isEqualTo(YearMonth.parse("2022-12")) },
            Executable { assertThat(beregnetKontantstøtte[3].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe.size).isEqualTo(0) },

            Executable { assertThat(beregnetKontantstøtte[4].inntektRapportering).isEqualTo(Inntektsrapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[4].visningsnavn).isEqualTo(Inntektsrapportering.KONTANTSTØTTE.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[4].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[4].periode.fom).isEqualTo(YearMonth.parse("2023-05")) },
            Executable { assertThat(beregnetKontantstøtte[4].periode.til).isNull() },
            Executable { assertThat(beregnetKontantstøtte[4].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe.size).isEqualTo(0) },
        )
    }
}
