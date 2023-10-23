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
class OvergangsstønadServiceTest {

    @Test
    fun `skal returnere Overgangsstønad når dagens dato er 2023-01-01`() {
        val dagensDato = LocalDate.of(2023, 1, 1)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val overgangsstønadService = OvergangsstønadService(fixedDateProvider)

        val overgangsstønad = TestUtil.byggOvergangsstønad()
        val beregnedeOvergangsstønader = overgangsstønadService.beregnOvergangsstønad(overgangsstønad)

        assertAll(
            Executable { assertNotNull(beregnedeOvergangsstønader) },
            Executable { assertThat(beregnedeOvergangsstønader.size).isEqualTo(3) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[0].visningsnavn).isEqualTo("${InntektRapportering.OVERGANGSSTØNAD.visningsnavn} 2021") },
            Executable { assertThat(beregnedeOvergangsstønader[0].sumInntekt).isEqualTo(BigDecimal.valueOf(100)) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-01"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe.size).isEqualTo(1) },
            Executable {
                assertThat(beregnedeOvergangsstønader[0].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    100
                )
            },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].kode).isEqualTo("overgangsstønad") },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].visningsnavn).isEqualTo("Overgangsstønad") },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].beløp.toInt()).isEqualTo(100) },

            Executable { assertThat(beregnedeOvergangsstønader[1].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND) },
            Executable { assertThat(beregnedeOvergangsstønader[1].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[1].sumInntekt).isEqualTo(BigDecimal.valueOf(7800)) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].inntektPostListe.size).isEqualTo(12) },
            Executable {
                assertThat(beregnedeOvergangsstønader[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    7800
                )
            },

            Executable { assertThat(beregnedeOvergangsstønader[2].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND) },
            Executable { assertThat(beregnedeOvergangsstønader[2].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[2].sumInntekt).isEqualTo(BigDecimal.valueOf(13200)) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-09"))) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnedeOvergangsstønader[2].inntektPostListe.size).isEqualTo(3) },
            Executable {
                assertThat(beregnedeOvergangsstønader[2].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    3300
                )
            }
        )
    }

    @Test
    fun `skal returnere Overgangsstønad når dagens dato er 2023-01-10`() {
        val dagensDato = LocalDate.of(2023, 1, 10)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val overgangsstønadService = OvergangsstønadService(fixedDateProvider)

        val overgangsstønadDto = TestUtil.byggOvergangsstønad()
        val beregnedeOvergangsstønader = overgangsstønadService.beregnOvergangsstønad(overgangsstønadDto)

        assertAll(
            Executable { assertNotNull(beregnedeOvergangsstønader) },
            Executable { assertThat(beregnedeOvergangsstønader.size).isEqualTo(4) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[0].visningsnavn).isEqualTo("${InntektRapportering.OVERGANGSSTØNAD.visningsnavn} 2021") },
            Executable { assertThat(beregnedeOvergangsstønader[0].sumInntekt).isEqualTo(BigDecimal.valueOf(100)) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-01"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe.size).isEqualTo(1) },
            Executable {
                assertThat(beregnedeOvergangsstønader[0].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    100
                )
            },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].kode).isEqualTo("overgangsstønad") },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].visningsnavn).isEqualTo("Overgangsstønad") },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].beløp.toInt()).isEqualTo(100) },

            Executable { assertThat(beregnedeOvergangsstønader[1].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[1].visningsnavn).isEqualTo("${InntektRapportering.OVERGANGSSTØNAD.visningsnavn} 2022") },
            Executable { assertThat(beregnedeOvergangsstønader[1].sumInntekt).isEqualTo(BigDecimal.valueOf(9000)) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].inntektPostListe.size).isEqualTo(12) },
            Executable {
                assertThat(beregnedeOvergangsstønader[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    9000
                )
            },

            Executable { assertThat(beregnedeOvergangsstønader[2].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND) },
            Executable { assertThat(beregnedeOvergangsstønader[2].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[2].sumInntekt).isEqualTo(BigDecimal.valueOf(9000)) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[2].inntektPostListe.size).isEqualTo(12) },
            Executable {
                assertThat(beregnedeOvergangsstønader[2].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    9000
                )
            },

            Executable { assertThat(beregnedeOvergangsstønader[3].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND) },
            Executable { assertThat(beregnedeOvergangsstønader[3].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[3].sumInntekt).isEqualTo(BigDecimal.valueOf(14400)) },
            Executable { assertThat(beregnedeOvergangsstønader[3].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-10"))) },
            Executable { assertThat(beregnedeOvergangsstønader[3].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[3].inntektPostListe.size).isEqualTo(3) },
            Executable {
                assertThat(beregnedeOvergangsstønader[3].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    3600
                )
            }
        )
    }

    @Test
    fun `skal returnere Overgangsstønad når dagens dato er 2023-09-01`() {
        val dagensDato = LocalDate.of(2023, 9, 1)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val overgangsstønadService = OvergangsstønadService(fixedDateProvider)

        val overgangsstønadDto = TestUtil.byggOvergangsstønad()
        val beregnedeOvergangsstønader = overgangsstønadService.beregnOvergangsstønad(overgangsstønadDto)

        assertAll(
            Executable { assertNotNull(beregnedeOvergangsstønader) },
            Executable { assertThat(beregnedeOvergangsstønader.size).isEqualTo(4) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[0].visningsnavn).isEqualTo("${InntektRapportering.OVERGANGSSTØNAD.visningsnavn} 2021") },
            Executable { assertThat(beregnedeOvergangsstønader[0].sumInntekt).isEqualTo(BigDecimal.valueOf(100)) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-01"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe.size).isEqualTo(1) },
            Executable {
                assertThat(beregnedeOvergangsstønader[0].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    100
                )
            },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].kode).isEqualTo("overgangsstønad") },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].visningsnavn).isEqualTo("Overgangsstønad") },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe[0].beløp.toInt()).isEqualTo(100) },

            Executable { assertThat(beregnedeOvergangsstønader[1].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[1].visningsnavn).isEqualTo("${InntektRapportering.OVERGANGSSTØNAD.visningsnavn} 2022") },
            Executable { assertThat(beregnedeOvergangsstønader[1].sumInntekt).isEqualTo(BigDecimal.valueOf(9000)) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-01"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].inntektPostListe.size).isEqualTo(12) },
            Executable {
                assertThat(beregnedeOvergangsstønader[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    9000
                )
            },

            Executable { assertThat(beregnedeOvergangsstønader[2].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND) },
            Executable { assertThat(beregnedeOvergangsstønader[2].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[2].sumInntekt).isEqualTo(BigDecimal.valueOf(11700)) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-08"))) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-07"))) },
            Executable { assertThat(beregnedeOvergangsstønader[2].inntektPostListe.size).isEqualTo(9) },
            Executable {
                assertThat(beregnedeOvergangsstønader[2].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    11700
                )
            },

            Executable { assertThat(beregnedeOvergangsstønader[3].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND) },
            Executable { assertThat(beregnedeOvergangsstønader[3].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[3].sumInntekt).isEqualTo(BigDecimal.ZERO) },
            Executable { assertThat(beregnedeOvergangsstønader[3].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2023-05"))) },
            Executable { assertThat(beregnedeOvergangsstønader[3].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2023-07"))) },
            Executable { assertThat(beregnedeOvergangsstønader[3].inntektPostListe.isEmpty()) }
        )
    }

    @Test
    fun `skal håndtere inntekter over nullperioder`() {
        val dagensDato = LocalDate.of(2023, 1, 1)

        val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
        val overgangsstønadService = OvergangsstønadService(fixedDateProvider)

        val overgangsstonadDto = TestUtil.byggOvergangsstønad()[0]
        val overgansstønadMedNullperiode = overgangsstonadDto.copy(periodeTil = overgangsstonadDto.periodeFra)
        val beregnedeOvergangsstønader =
            overgangsstønadService.beregnOvergangsstønad(listOf(overgansstønadMedNullperiode))

        assertAll(
            Executable { assertNotNull(beregnedeOvergangsstønader) },
            Executable { assertThat(beregnedeOvergangsstønader.size).isEqualTo(2) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND) },
            Executable { assertThat(beregnedeOvergangsstønader[0].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_12MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[0].sumInntekt).isEqualTo(BigDecimal.valueOf(0)) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2021-12"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe.size).isEqualTo(0) },
            Executable {
                assertThat(beregnedeOvergangsstønader[0].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    0
                )
            },

            Executable { assertThat(beregnedeOvergangsstønader[1].inntektRapportering).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND) },
            Executable { assertThat(beregnedeOvergangsstønader[1].visningsnavn).isEqualTo(InntektRapportering.OVERGANGSSTØNAD_BEREGNET_3MND.visningsnavn) },
            Executable { assertThat(beregnedeOvergangsstønader[1].sumInntekt).isEqualTo(BigDecimal.valueOf(0)) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeFra).isEqualTo(FomMåned(YearMonth.parse("2022-09"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeTom).isEqualTo(TomMåned(YearMonth.parse("2022-11"))) },
            Executable { assertThat(beregnedeOvergangsstønader[1].inntektPostListe.size).isEqualTo(0) },
            Executable {
                assertThat(beregnedeOvergangsstønader[1].inntektPostListe.sumOf { it.beløp.toInt() }).isEqualTo(
                    0
                )
            }
        )
    }

    @Test
    fun `skal teste avrunding ved beregning av månedsbeløp`() {
        val beløp = beregneBeløpPerMåned(BigDecimal.valueOf(1000), 3)
        Executable { assertThat(beløp).isEqualTo(BigDecimal.valueOf(333)) }
    }
}
