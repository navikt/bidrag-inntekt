package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.domene.util.visningsnavn
import no.nav.bidrag.inntekt.TestUtil
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

@DisplayName("AinntektServiceTest")
class AinntektServiceTest : AbstractServiceTest() {

    private final val filnavnEksempelRequest = "src/test/resources/testfiler/eksempel_request.json"

    private final val inntektRequest = TestUtil.byggInntektRequest(filnavnEksempelRequest)

    @Nested
    internal inner class BeregnÅrsinntekt {

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere årsinntekter når dagens dato er 2023-01-01`() {
            val ainntektHentetDato = LocalDate.of(2023, 1, 1)

            val ainntektService = AinntektService()

            val transformerteInntekter =
                ainntektService.beregnAarsinntekt(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 3) },

                Executable { assertTrue(transformerteInntekter[0].inntektRapportering == Inntektsrapportering.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == "${Inntektsrapportering.AINNTEKT.visningsnavn.intern} 2021") },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
                Executable { assertEquals(YearMonth.of(2021, 1), transformerteInntekter[0].periode.fom) },
                Executable { assertEquals(YearMonth.of(2021, 12), transformerteInntekter[0].periode.til) },
                Executable { assertTrue(transformerteInntekter[0].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 4000) },

                Executable { assertTrue(transformerteInntekter[1].inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_12MND) },
                Executable { assertTrue(transformerteInntekter[1].visningsnavn == Inntektsrapportering.AINNTEKT_BEREGNET_12MND.visningsnavn.intern) },
                Executable { assertEquals(BigDecimal.valueOf(393000.789), transformerteInntekter[1].sumInntekt) },
                Executable { assertEquals(YearMonth.of(2021, 12), transformerteInntekter[1].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 11), transformerteInntekter[1].periode.til) },
                Executable { assertTrue(transformerteInntekter[1].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 393000) },

                Executable { assertTrue(transformerteInntekter[2].inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_3MND) },
                Executable { assertTrue(transformerteInntekter[2].visningsnavn == Inntektsrapportering.AINNTEKT_BEREGNET_3MND.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == BigDecimal.valueOf(660000)) },
                Executable { assertEquals(YearMonth.of(2022, 9), transformerteInntekter[2].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 11), transformerteInntekter[2].periode.til) },
                Executable { assertTrue(transformerteInntekter[2].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.sumOf { it.beløp.toInt() } == 660000) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].kode == "fastloenn") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].visningsnavn == "Fastlønn") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].beløp.toInt() == 660000) },
            )
        }

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere årsinntekter når dagens dato er 2023-01-10`() {
            val ainntektHentetDato = LocalDate.of(2023, 1, 10)

            val ainntektService = AinntektService()

            val transformerteInntekter =
                ainntektService.beregnAarsinntekt(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 4) },

                Executable { assertTrue(transformerteInntekter[0].inntektRapportering == Inntektsrapportering.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == "${Inntektsrapportering.AINNTEKT.visningsnavn.intern} 2021") },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
                Executable { assertEquals(YearMonth.of(2021, 1), transformerteInntekter[0].periode.fom) },
                Executable { assertEquals(YearMonth.of(2021, 12), transformerteInntekter[0].periode.til) },
                Executable { assertTrue(transformerteInntekter[0].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 4000) },

                Executable { assertTrue(transformerteInntekter[1].inntektRapportering == Inntektsrapportering.AINNTEKT) },
                Executable {
                    assertEquals(
                        "${Inntektsrapportering.AINNTEKT.visningsnavn.intern} 2022",
                        transformerteInntekter[1].visningsnavn,
                    )
                },
                Executable { assertEquals(BigDecimal.valueOf(446000.789), transformerteInntekter[1].sumInntekt) },
                Executable { assertEquals(YearMonth.of(2022, 1), transformerteInntekter[1].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 12), transformerteInntekter[1].periode.til) },
                Executable { assertTrue(transformerteInntekter[1].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

                Executable { assertTrue(transformerteInntekter[2].inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_12MND) },
                Executable { assertTrue(transformerteInntekter[2].visningsnavn == Inntektsrapportering.AINNTEKT_BEREGNET_12MND.visningsnavn.intern) },
                Executable { assertEquals(BigDecimal.valueOf(446000.789), transformerteInntekter[2].sumInntekt) },
                Executable { assertEquals(YearMonth.of(2022, 1), transformerteInntekter[2].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 12), transformerteInntekter[2].periode.til) },
                Executable { assertTrue(transformerteInntekter[2].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

                Executable { assertTrue(transformerteInntekter[3].inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_3MND) },
                Executable { assertTrue(transformerteInntekter[3].visningsnavn == Inntektsrapportering.AINNTEKT_BEREGNET_3MND.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[3].sumInntekt == BigDecimal.valueOf(660000)) },
                Executable { assertEquals(YearMonth.of(2022, 10), transformerteInntekter[3].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 12), transformerteInntekter[3].periode.til) },
                Executable { assertTrue(transformerteInntekter[3].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.sumOf { it.beløp.toInt() } == 660000) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe[0].kode == "fastloenn") },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe[0].visningsnavn == "Fastlønn") },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe[0].beløp.toInt() == 660000) },
            )
        }

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal transformere årsinntekter når dagens dato er 2023-09-01`() {
            val ainntektHentetDato = LocalDate.of(2023, 9, 1)

            val ainntektService = AinntektService()

            val transformerteInntekter =
                ainntektService.beregnAarsinntekt(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 4) },

                Executable { assertTrue(transformerteInntekter[0].inntektRapportering == Inntektsrapportering.AINNTEKT) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == "${Inntektsrapportering.AINNTEKT.visningsnavn.intern} 2021") },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
                Executable { assertEquals(YearMonth.of(2021, 1), transformerteInntekter[0].periode.fom) },
                Executable { assertEquals(YearMonth.of(2021, 12), transformerteInntekter[0].periode.til) },
                Executable { assertTrue(transformerteInntekter[0].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 4000) },

                Executable { assertTrue(transformerteInntekter[1].inntektRapportering == Inntektsrapportering.AINNTEKT) },
                Executable {
                    assertEquals(
                        "${Inntektsrapportering.AINNTEKT.visningsnavn.intern} 2022",
                        transformerteInntekter[1].visningsnavn,
                    )
                },
                Executable { assertEquals(BigDecimal.valueOf(446000.789), transformerteInntekter[1].sumInntekt) },
                Executable { assertEquals(YearMonth.of(2022, 1), transformerteInntekter[1].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 12), transformerteInntekter[1].periode.til) },
                Executable { assertTrue(transformerteInntekter[1].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

                Executable { assertTrue(transformerteInntekter[2].inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_12MND) },
                Executable { assertTrue(transformerteInntekter[2].visningsnavn == Inntektsrapportering.AINNTEKT_BEREGNET_12MND.visningsnavn.intern) },
                Executable { assertEquals(BigDecimal.valueOf(743001.119), transformerteInntekter[2].sumInntekt) },
                Executable { assertEquals(YearMonth.of(2022, 8), transformerteInntekter[2].periode.fom) },
                Executable { assertEquals(YearMonth.of(2023, 7), transformerteInntekter[2].periode.til) },
                Executable { assertTrue(transformerteInntekter[2].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 4) },
                Executable { assertEquals(BigDecimal.valueOf(743001), transformerteInntekter[2].inntektPostListe.sumOf { it.beløp }) },

                Executable { assertTrue(transformerteInntekter[3].inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_3MND) },
                Executable { assertTrue(transformerteInntekter[3].visningsnavn == Inntektsrapportering.AINNTEKT_BEREGNET_3MND.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[3].sumInntekt == BigDecimal.valueOf(880000)) },
                Executable { assertEquals(YearMonth.of(2023, 5), transformerteInntekter[3].periode.fom) },
                Executable { assertEquals(YearMonth.of(2023, 7), transformerteInntekter[3].periode.til) },
                Executable { assertTrue(transformerteInntekter[3].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.size == 3) },
                Executable { assertTrue(transformerteInntekter[3].inntektPostListe.sumOf { it.beløp.toInt() } == 880000) },
            )
        }

        @Test
        fun `summert årsinntekt skal bli null dersom periode for månedsinntekter er null`() {
            val ainntektHentetDato = LocalDate.of(2023, 9, 1)
            val ainntektService = AinntektService()

            val ainntektspost = inntektRequest.ainntektsposter[0]
            val ainntektspostMedOpptjeningsperiodeTilLikFra =
                ainntektspost.copy(opptjeningsperiodeTil = ainntektspost.opptjeningsperiodeFra)

            val transformerteInntekter =
                ainntektService.beregnAarsinntekt(listOf(ainntektspostMedOpptjeningsperiodeTilLikFra), ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == 2) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(0)) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(0)) },
            )
        }
    }

    @Nested
    internal inner class BeregnMånedsinntekt {
        @Test
        fun `skal transformere månedsinntekter`() {
            val ainntektHentetDato = LocalDate.of(2023, 9, 1)
            val ainntektService = AinntektService()

            val transformerteInntekter =
                ainntektService.beregnMaanedsinntekt(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == 20) },
                Executable {
                    assertTrue(
                        transformerteInntekter.filter { it.gjelderÅrMåned.year == 2021 }
                            .sumOf { it.sumInntekt.toInt() } == 4000,
                    )
                },
                Executable {
                    assertTrue(
                        transformerteInntekter.filter { it.gjelderÅrMåned.year == 2022 }
                            .sumOf { it.sumInntekt.toInt() } == 446000,
                    )
                },
                Executable {
                    assertTrue(
                        transformerteInntekter.filter { it.gjelderÅrMåned.year == 2023 }
                            .sumOf { it.sumInntekt.toInt() } == 468000,
                    )
                },

                Executable { assertTrue(transformerteInntekter[0].gjelderÅrMåned == YearMonth.of(2021, 11)) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(2000)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 2000) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp.toInt() == 2000) },
            )
        }

        @Test
        fun `skal ikke transformere noen månedsinntekter dersom perioden er på null måneder`() {
            val ainntektHentetDato = LocalDate.of(2023, 9, 1)
            val ainntektService = AinntektService()

            val ainntektspost = inntektRequest.ainntektsposter[0]
            val ainntektspostMedOpptjeningsperiodeTilLikFra =
                ainntektspost.copy(opptjeningsperiodeTil = ainntektspost.opptjeningsperiodeFra)

            val transformerteInntekter =
                ainntektService.beregnMaanedsinntekt(listOf(ainntektspostMedOpptjeningsperiodeTilLikFra), ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.isEmpty()) },
            )
        }

        @Test
        fun `skal runde beregnet månedsinntekt opp hvis den er et desimaltall med minst fem tideler`() {
            val periodeIMnd: Long = 3
            val tremånederslønnInt = 1234565
            val tremånederslønn = BigDecimal(tremånederslønnInt)
            val månedslønnInt = tremånederslønnInt.div(3)
            val månedslønn = tremånederslønn.div(BigDecimal(periodeIMnd))

            // Verifisere at BigDecimal månedslønn er rundet opp til nærmeste heltall
            assertTrue(månedslønn.toInt() - månedslønnInt == 1)

            val ainntektHentetDato = LocalDate.of(2023, 9, 1)
            val ainntektService = AinntektService()

            val ainntektspost = inntektRequest.ainntektsposter[0]
            val ainntektspostMedOpptjeningsperiodeTilLikFra =
                ainntektspost.copy(
                    opptjeningsperiodeTil = ainntektspost.opptjeningsperiodeFra?.plusMonths(periodeIMnd),
                    beløp = tremånederslønn,
                )

            val transformerteInntekter =
                ainntektService.beregnMaanedsinntekt(listOf(ainntektspostMedOpptjeningsperiodeTilLikFra), ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == periodeIMnd.toInt()) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == månedslønn) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp == månedslønn) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == månedslønn) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].beløp == månedslønn) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == månedslønn) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].beløp == månedslønn) },
            )
        }

        @Test
        fun `skal runde beregnet månedsinntekt ned hvis den er et desimaltall med færre enn fem tideler`() {
            val periodeIMnd: Long = 3
            val tremånederslønnInt = 1234561
            val tremånederslønn = BigDecimal(tremånederslønnInt)
            val månedslønnInt = tremånederslønnInt.div(3)
            val månedslønn = tremånederslønn.div(BigDecimal(periodeIMnd))

            // Verifisere at BigDecimal månedslønn er rundet ned til nærmeste heltall tilsvarende som for Int
            assertTrue(månedslønn.toInt() - månedslønnInt == 0)

            val ainntektHentetDato = LocalDate.of(2023, 9, 1)
            val ainntektService = AinntektService()

            val ainntektspost = inntektRequest.ainntektsposter[0]
            val ainntektspostMedOpptjeningsperiodeTilLikFra =
                ainntektspost.copy(
                    opptjeningsperiodeTil = ainntektspost.opptjeningsperiodeFra?.plusMonths(periodeIMnd),
                    beløp = tremånederslønn,
                )

            val transformerteInntekter =
                ainntektService.beregnMaanedsinntekt(listOf(ainntektspostMedOpptjeningsperiodeTilLikFra), ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },
                Executable { assertTrue(transformerteInntekter.size == periodeIMnd.toInt()) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == månedslønn) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp == månedslønn) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == månedslønn) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].beløp == månedslønn) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == månedslønn) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[2].inntektPostListe[0].beløp == månedslønn) },
            )
        }
    }
}
