package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektRapportering
import no.nav.bidrag.domain.tid.FomMåned
import no.nav.bidrag.domain.tid.TomMåned
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.FixedDateProvider
import no.nav.bidrag.inntekt.util.beregneBeløpPerMåned
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
import java.time.LocalDate
import java.time.YearMonth

@Suppress("NonAsciiCharacters")
@DisplayName("OvergangsstønadServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class KontantstøtteServiceTest {

    @Test
    fun `skal returnere kontantstøtte når dagens dato er 2023-01-01`() {
        val dagensDato = LocalDate.of(2023, 1, 1)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val kontantstøtteService = KontantstøtteService(fixedDateProvider)

        val kontantstøtte = TestUtil.byggKontantstøtte()
        val beregnetKontantstøtte = kontantstøtteService.beregnKontantstøtte(kontantstøtte)

        assertAll(
            Executable { assertNotNull(beregnetKontantstøtte) },
            Executable { assertThat(beregnetKontantstøtte.size).isEqualTo(5) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[0].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2021") },
            Executable { assertThat(beregnetKontantstøtte[0].sumInntekt).isEqualTo(BigDecimal.valueOf(15000)) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-01"))) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnetKontantstøtte[0].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].beløp.toInt()).isEqualTo(15000) },

            Executable { assertThat(beregnetKontantstøtte[1].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[1].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[1].sumInntekt).isEqualTo(BigDecimal.valueOf(67500)) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnetKontantstøtte[1].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(67500) },

            Executable { assertThat(beregnetKontantstøtte[2].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[2].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[2].sumInntekt).isEqualTo(BigDecimal.valueOf(60000)) },
            Executable { assertThat(beregnetKontantstøtte[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-09"))) },
            Executable { assertThat(beregnetKontantstøtte[2].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnetKontantstøtte[2].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe[0].beløp.toInt()).isEqualTo(15000) },

            Executable { assertThat(beregnetKontantstøtte[3].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[3].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[3].sumInntekt).isEqualTo(BigDecimal.valueOf(22500)) },
            Executable { assertThat(beregnetKontantstøtte[3].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnetKontantstøtte[3].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnetKontantstøtte[3].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe[0].beløp.toInt()).isEqualTo(22500) },

            Executable { assertThat(beregnetKontantstøtte[4].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[4].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[4].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[4].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-09"))) },
            Executable { assertThat(beregnetKontantstøtte[4].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnetKontantstøtte[4].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].beløp.toInt()).isEqualTo(22500) }
        )
    }

    @Test
    fun `skal returnere kontantstøtte når dagens dato er 2023-01-10`() {
        val dagensDato = LocalDate.of(2023, 1, 10)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val kontantstøtteService = KontantstøtteService(fixedDateProvider)

        val kontantstøtte = TestUtil.byggKontantstøtte()
        val beregnetKontantstøtte = kontantstøtteService.beregnKontantstøtte(kontantstøtte)

        assertAll(
            Executable { assertNotNull(beregnetKontantstøtte) },
            Executable { assertThat(beregnetKontantstøtte.size).isEqualTo(7) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[0].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2021") },
            Executable { assertThat(beregnetKontantstøtte[0].sumInntekt).isEqualTo(BigDecimal.valueOf(15000)) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-01"))) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnetKontantstøtte[0].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].beløp.toInt()).isEqualTo(15000) },

            Executable { assertThat(beregnetKontantstøtte[1].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[1].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2022") },
            Executable { assertThat(beregnetKontantstøtte[1].sumInntekt).isEqualTo(BigDecimal.valueOf(67500)) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[1].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(67500) },

            Executable { assertThat(beregnetKontantstøtte[2].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[2].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[2].sumInntekt).isEqualTo(BigDecimal.valueOf(67500)) },
            Executable { assertThat(beregnetKontantstøtte[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnetKontantstøtte[2].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[2].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(67500) },

            Executable { assertThat(beregnetKontantstøtte[3].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[3].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[3].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[3].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-10"))) },
            Executable { assertThat(beregnetKontantstøtte[3].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[3].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe[0].beløp.toInt()).isEqualTo(22500) },

            Executable { assertThat(beregnetKontantstøtte[4].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[4].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2022") },
            Executable { assertThat(beregnetKontantstøtte[4].sumInntekt).isEqualTo(BigDecimal.valueOf(30000)) },
            Executable { assertThat(beregnetKontantstøtte[4].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnetKontantstøtte[4].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[4].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].beløp.toInt()).isEqualTo(30000) },

            Executable { assertThat(beregnetKontantstøtte[5].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[5].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[5].sumInntekt).isEqualTo(BigDecimal.valueOf(30000)) },
            Executable { assertThat(beregnetKontantstøtte[5].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnetKontantstøtte[5].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[5].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[5].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[5].inntektPostListe[0].beløp.toInt()).isEqualTo(30000) },

            Executable { assertThat(beregnetKontantstøtte[6].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[6].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[6].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[6].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-10"))) },
            Executable { assertThat(beregnetKontantstøtte[6].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[6].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[6].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[6].inntektPostListe[0].beløp.toInt()).isEqualTo(22500) }
        )
    }

    @Test
    fun `skal returnere kontantstøtte når dagens dato er 2023-09-01`() {
        val dagensDato = LocalDate.of(2023, 9, 1)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val kontantstøtteService = KontantstøtteService(fixedDateProvider)

        val kontantstøtte = TestUtil.byggKontantstøtte()
        val beregnetKontantstøtte = kontantstøtteService.beregnKontantstøtte(kontantstøtte)

        assertAll(
            Executable { assertNotNull(beregnetKontantstøtte) },
            Executable { assertThat(beregnetKontantstøtte.size).isEqualTo(7) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[0].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2021") },
            Executable { assertThat(beregnetKontantstøtte[0].sumInntekt).isEqualTo(BigDecimal.valueOf(15000)) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-01"))) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnetKontantstøtte[0].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe[0].beløp.toInt()).isEqualTo(15000) },

            Executable { assertThat(beregnetKontantstøtte[1].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[1].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2022") },
            Executable { assertThat(beregnetKontantstøtte[1].sumInntekt).isEqualTo(BigDecimal.valueOf(67500)) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[1].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(67500) },

            Executable { assertThat(beregnetKontantstøtte[2].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[2].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[2].sumInntekt).isEqualTo(BigDecimal.valueOf(52500)) },
            Executable { assertThat(beregnetKontantstøtte[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-08"))) },
            Executable { assertThat(beregnetKontantstøtte[2].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-07"))) },
            Executable { assertThat(beregnetKontantstøtte[2].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[2].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(52500) },

            Executable { assertThat(beregnetKontantstøtte[3].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[3].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[3].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[3].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2023-05"))) },
            Executable { assertThat(beregnetKontantstøtte[3].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-07"))) },
            Executable { assertThat(beregnetKontantstøtte[3].gjelderBarnPersonId).isEqualTo("12345678901") },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[3].inntektPostListe[0].beløp.toInt()).isEqualTo(22500) },

            Executable { assertThat(beregnetKontantstøtte[4].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE) },
            Executable { assertThat(beregnetKontantstøtte[4].visningsnavn).isEqualTo("${InntektRapportering.KONTANTSTØTTE.visningsnavn} 2022") },
            Executable { assertThat(beregnetKontantstøtte[4].sumInntekt).isEqualTo(BigDecimal.valueOf(30000)) },
            Executable { assertThat(beregnetKontantstøtte[4].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnetKontantstøtte[4].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnetKontantstøtte[4].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].kode).isEqualTo("kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].visningsnavn).isEqualTo("Kontantstøtte") },
            Executable { assertThat(beregnetKontantstøtte[4].inntektPostListe[0].beløp.toInt()).isEqualTo(30000) },

            Executable { assertThat(beregnetKontantstøtte[5].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[5].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[5].sumInntekt).isEqualTo(BigDecimal.valueOf(52500)) },
            Executable { assertThat(beregnetKontantstøtte[5].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-08"))) },
            Executable { assertThat(beregnetKontantstøtte[5].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-07"))) },
            Executable { assertThat(beregnetKontantstøtte[5].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[5].inntektPostListe.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[5].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(52500) },

            Executable { assertThat(beregnetKontantstøtte[6].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[6].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[6].sumInntekt).isEqualTo(BigDecimal.valueOf(90000)) },
            Executable { assertThat(beregnetKontantstøtte[6].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2023-05"))) },
            Executable { assertThat(beregnetKontantstøtte[6].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-07"))) },
            Executable { assertThat(beregnetKontantstøtte[6].gjelderBarnPersonId).isEqualTo("98765432109") },
            Executable { assertThat(beregnetKontantstøtte[6].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnetKontantstøtte[6].inntektPostListe[0].beløp.toInt()).isEqualTo(22500) }
        )
    }

    @Test
    fun `skal håndtere inntekter over nullperioder`() {
        val dagensDato = LocalDate.of(2024, 1, 12)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val kontantstøtteService = KontantstøtteService(fixedDateProvider)

        val kontantstøtte = TestUtil.byggKontantstøtte()[0]
        val kontantstøtteMedNullperiode = kontantstøtte.copy(periodeTil = kontantstøtte.periodeFra)

        val beregnetKontantstøtte = kontantstøtteService.beregnKontantstøtte(listOf(kontantstøtteMedNullperiode))

        assertAll(
            Executable { assertNotNull(beregnetKontantstøtte) },
            Executable { assertThat(beregnetKontantstøtte.size).isEqualTo(2) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND) },
            Executable { assertThat(beregnetKontantstøtte[0].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[0].sumInntekt).isEqualTo(BigDecimal.valueOf(0)) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2023-01"))) },
            Executable { assertThat(beregnetKontantstøtte[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-12"))) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe.size).isEqualTo(0) },
            Executable { assertThat(beregnetKontantstøtte[0].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(0) },

            Executable { assertThat(beregnetKontantstøtte[1].inntektRapportering).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND) },
            Executable { assertThat(beregnetKontantstøtte[1].visningsnavn).isEqualTo(InntektRapportering.KONTANTSTØTTE_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnetKontantstøtte[1].sumInntekt).isEqualTo(BigDecimal.valueOf(0)) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2023-10"))) },
            Executable { assertThat(beregnetKontantstøtte[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-12"))) },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.size).isEqualTo(0) },
            Executable { assertThat(beregnetKontantstøtte[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(0) }
        )
    }

    @Test
    fun `skal teste avrunding ved beregning av månedsbeløp`() {
        val beløp = beregneBeløpPerMåned(BigDecimal.valueOf(1000), 3)
        Executable { assertThat(beløp).isEqualTo(BigDecimal.valueOf(333)) }
    }
}
