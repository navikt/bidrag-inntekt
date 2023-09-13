package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.FixedDateProvider
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

@DisplayName("AinntektServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class AinntektServiceTest {

    private final val filnavnKodeverkLoennsbeskrivelser = "src/test/resources/testfiler/respons_kodeverk_loennsbeskrivelser.json"
    private final val filnavnEksempelRequest = "src/test/resources/testfiler/eksempel_request.json"

    private final val inntektRequest = TestUtil.byggInntektRequest(filnavnEksempelRequest)
    private final val kodeverkResponse = TestUtil.byggKodeverkResponse(filnavnKodeverkLoennsbeskrivelser)

    @Nested
    internal inner class BeregnÅrsinntekt {
        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere årsinntekter når dagens dato er 2023-01-01`() {
            val dagensDato = LocalDate.of(2023, 1, 1)

            val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
            val ainntektService = AinntektService(fixedDateProvider)

            val transformerteInntekter = ainntektService.beregnAarsinntekt(inntektRequest.ainntektListe, kodeverkResponse)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 3) },

                Executable { assertTrue(transformerteInntekter[0].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == "${InntektBeskrivelse.AINNTEKT.visningsnavn} 2021") },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
                Executable { assertTrue(transformerteInntekter[0].periodeFra == YearMonth.of(2021, 1)) },
                Executable { assertTrue(transformerteInntekter[0].periodeTil == YearMonth.of(2021, 12)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 4000) },

                Executable { assertTrue(transformerteInntekter[1].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND) },
                Executable { assertTrue(transformerteInntekter[1].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND.visningsnavn) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(393000)) },
                Executable { assertTrue(transformerteInntekter[1].periodeFra == YearMonth.of(2021, 12)) },
                Executable { assertTrue(transformerteInntekter[1].periodeTil == YearMonth.of(2022, 11)) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 393000) },

                Executable { assertTrue(transformerteInntekter[2].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND) },
                Executable { assertTrue(transformerteInntekter[2].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND.visningsnavn) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == BigDecimal.valueOf(660000)) },
                Executable { assertTrue(transformerteInntekter[2].periodeFra == YearMonth.of(2022, 9)) },
                Executable { assertTrue(transformerteInntekter[2].periodeTil == YearMonth.of(2022, 11)) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.sumOf { it.beløp.toInt() } == 660000) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].kode == "fastloenn") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].visningsnavn == "Fastlønn") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].beløp.toInt() == 660000) }
            )
        }

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere årsinntekter når dagens dato er 2023-01-10`() {
            val dagensDato = LocalDate.of(2023, 1, 10)

            val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
            val ainntektService = AinntektService(fixedDateProvider)

            val transformerteInntekter = ainntektService.beregnAarsinntekt(inntektRequest.ainntektListe, kodeverkResponse)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 4) },

                Executable { assertTrue(transformerteInntekter[0].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == "${InntektBeskrivelse.AINNTEKT.visningsnavn} 2021") },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
                Executable { assertTrue(transformerteInntekter[0].periodeFra == YearMonth.of(2021, 1)) },
                Executable { assertTrue(transformerteInntekter[0].periodeTil == YearMonth.of(2021, 12)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 4000) },

                Executable { assertTrue(transformerteInntekter[1].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[1].visningsnavn == "${InntektBeskrivelse.AINNTEKT.visningsnavn} 2022") },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(446000)) },
                Executable { assertTrue(transformerteInntekter[1].periodeFra == YearMonth.of(2022, 1)) },
                Executable { assertTrue(transformerteInntekter[1].periodeTil == YearMonth.of(2022, 12)) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

                Executable { assertTrue(transformerteInntekter[2].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND) },
                Executable { assertTrue(transformerteInntekter[2].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND.visningsnavn) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == BigDecimal.valueOf(446000)) },
                Executable { assertTrue(transformerteInntekter[2].periodeFra == YearMonth.of(2022, 1)) },
                Executable { assertTrue(transformerteInntekter[2].periodeTil == YearMonth.of(2022, 12)) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

                Executable { assertTrue(transformerteInntekter[3].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND) },
                Executable { assertTrue(transformerteInntekter[3].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND.visningsnavn) },
                Executable { assertTrue(transformerteInntekter[3].sumInntekt == BigDecimal.valueOf(660000)) },
                Executable { assertTrue(transformerteInntekter[3].periodeFra == YearMonth.of(2022, 10)) },
                Executable { assertTrue(transformerteInntekter[3].periodeTil == YearMonth.of(2022, 12)) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.sumOf { it.beløp.toInt() } == 660000) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe[0].kode == "fastloenn") },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe[0].visningsnavn == "Fastlønn") },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe[0].beløp.toInt() == 660000) }
            )
        }

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere årsinntekter når dagens dato er 2023-09-01`() {
            val dagensDato = LocalDate.of(2023, 9, 1)

            val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
            val ainntektService = AinntektService(fixedDateProvider)

            val transformerteInntekter = ainntektService.beregnAarsinntekt(inntektRequest.ainntektListe, kodeverkResponse)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 4) },

                Executable { assertTrue(transformerteInntekter[0].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == "${InntektBeskrivelse.AINNTEKT.visningsnavn} 2021") },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
                Executable { assertTrue(transformerteInntekter[0].periodeFra == YearMonth.of(2021, 1)) },
                Executable { assertTrue(transformerteInntekter[0].periodeTil == YearMonth.of(2021, 12)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 4000) },

                Executable { assertTrue(transformerteInntekter[1].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[1].visningsnavn == "${InntektBeskrivelse.AINNTEKT.visningsnavn} 2022") },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(446000)) },
                Executable { assertTrue(transformerteInntekter[1].periodeFra == YearMonth.of(2022, 1)) },
                Executable { assertTrue(transformerteInntekter[1].periodeTil == YearMonth.of(2022, 12)) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

                Executable { assertTrue(transformerteInntekter[2].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND) },
                Executable { assertTrue(transformerteInntekter[2].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND.visningsnavn) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == BigDecimal.valueOf(743000)) },
                Executable { assertTrue(transformerteInntekter[2].periodeFra == YearMonth.of(2022, 8)) },
                Executable { assertTrue(transformerteInntekter[2].periodeTil == YearMonth.of(2023, 7)) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 4) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.sumOf { it.beløp.toInt() } == 743000) },

                Executable { assertTrue(transformerteInntekter[3].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND) },
                Executable { assertTrue(transformerteInntekter[3].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND.visningsnavn) },
                Executable { assertTrue(transformerteInntekter[3].sumInntekt == BigDecimal.valueOf(880000)) },
                Executable { assertTrue(transformerteInntekter[3].periodeFra == YearMonth.of(2023, 5)) },
                Executable { assertTrue(transformerteInntekter[3].periodeTil == YearMonth.of(2023, 7)) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.sumOf { it.beløp.toInt() } == 880000) }
            )
        }

        @Test
        fun `summert årsinntekt skal bli null dersom periode for månedsinntekter er null`() {
            val fixedDateProvider: DateProvider = FixedDateProvider(LocalDate.of(2023, 9, 1))
            val ainntektService = AinntektService(fixedDateProvider)

            var ainntektForGyldigPeriode = inntektRequest.ainntektListe[0]
            var ainntektspost = ainntektForGyldigPeriode.ainntektspostListe[0]
            var ainntektspostMedOpptjeningsperiodeTilLikFra = ainntektspost.copy(opptjeningsperiodeTil = ainntektspost.opptjeningsperiodeFra)
            var ainntektForNullperiode = ainntektForGyldigPeriode.copy(periodeTil = ainntektForGyldigPeriode.periodeFra, ainntektspostListe = listOf(ainntektspostMedOpptjeningsperiodeTilLikFra))

            val transformerteInntekter = ainntektService.beregnAarsinntekt(listOf(ainntektForNullperiode), kodeverkResponse)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == 2) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(0)) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(0)) }
            )
        }
    }

    @Nested
    internal inner class BeregnMånedsinntekt {
        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere månedsinntekter`() {
            val fixedDateProvider: DateProvider = FixedDateProvider(LocalDate.of(2023, 9, 1))
            val ainntektService = AinntektService(fixedDateProvider)

            val transformerteInntekter = ainntektService.beregnMaanedsinntekt(inntektRequest.ainntektListe, kodeverkResponse)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == 20) },
                Executable { assertTrue(transformerteInntekter.filter { it.periode.year == 2021 }.sumOf { it.sumInntekt.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter.filter { it.periode.year == 2022 }.sumOf { it.sumInntekt.toInt() } == 446000) },
                Executable { assertTrue(transformerteInntekter.filter { it.periode.year == 2023 }.sumOf { it.sumInntekt.toInt() } == 468000) },

                Executable { assertTrue(transformerteInntekter[0].periode == YearMonth.of(2021, 11)) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(2000)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 2000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 2000) }
            )
        }

        @Test
        fun `skal ikke transformere noen månedsinntekter dersom perioden er på null måneder`() {
            val fixedDateProvider: DateProvider = FixedDateProvider(LocalDate.of(2023, 9, 1))
            val ainntektService = AinntektService(fixedDateProvider)

            var ainntektForGyldigPeriode = inntektRequest.ainntektListe[0]
            var ainntektspost = ainntektForGyldigPeriode.ainntektspostListe[0]
            var ainntektspostMedOpptjeningsperiodeTilLikFra = ainntektspost.copy(opptjeningsperiodeTil = ainntektspost.opptjeningsperiodeFra)
            var ainntektForNullperiode = ainntektForGyldigPeriode.copy(periodeTil = ainntektForGyldigPeriode.periodeFra, ainntektspostListe = listOf(ainntektspostMedOpptjeningsperiodeTilLikFra))

            val transformerteInntekter = ainntektService.beregnMaanedsinntekt(listOf(ainntektForNullperiode), kodeverkResponse)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == 0) }
            )
        }
    }
}
