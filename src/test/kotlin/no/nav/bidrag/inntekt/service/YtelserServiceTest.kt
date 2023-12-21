package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.domene.util.visningsnavn
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.FixedDateProvider
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

@DisplayName("YtelserServiceTest")
class YtelserServiceTest : AbstractServiceTest() {

    @Nested
    internal inner class BeregnÅrsinntektAap {

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal summere aap-inntekter fra ainntekter`() {
            val filnavnYtelserAapRequest = "src/test/resources/testfiler/eksempel_request_aap.json"
            val inntektRequest = TestUtil.byggInntektRequest(filnavnYtelserAapRequest)

            val dagensDato = LocalDate.of(2023, 12, 17)
            val ainntektHentetDato = LocalDate.of(2023, 12, 17)

            val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
            val ytelserService = YtelserService(fixedDateProvider)

            val transformerteInntekter =
                ytelserService.beregnYtelser(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 2) },

                Executable { assertTrue(transformerteInntekter[0].inntektRapportering == Inntektsrapportering.AAP) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == Inntektsrapportering.AAP.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(50000)) },
                Executable { assertEquals(YearMonth.of(2021, 1), transformerteInntekter[0].periode.fom) },
                Executable { assertEquals(YearMonth.of(2021, 12), transformerteInntekter[0].periode.til) },
                Executable { assertTrue(transformerteInntekter[0].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp } == BigDecimal.valueOf(50000)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "arbeidsavklaringspenger") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Arbeidsavklaringspenger") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp == BigDecimal.valueOf(50000)) },

                Executable { assertTrue(transformerteInntekter[1].inntektRapportering == Inntektsrapportering.AAP) },
                Executable { assertTrue(transformerteInntekter[1].visningsnavn == Inntektsrapportering.AAP.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(78500.789)) },
                Executable { assertEquals(YearMonth.of(2022, 1), transformerteInntekter[1].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 12), transformerteInntekter[1].periode.til) },
                Executable { assertTrue(transformerteInntekter[1].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp } == BigDecimal.valueOf(78500.789)) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].kode == "arbeidsavklaringspenger") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].visningsnavn == "Arbeidsavklaringspenger") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].beløp == BigDecimal.valueOf(78500.789)) },

            )
        }

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal summere dagpenger-inntekter fra ainntekter`() {
            val filnavnYtelserDagpengerRequest = "src/test/resources/testfiler/eksempel_request_dagpenger.json"
            val inntektRequest = TestUtil.byggInntektRequest(filnavnYtelserDagpengerRequest)

            val dagensDato = LocalDate.of(2023, 12, 17)
            val ainntektHentetDato = LocalDate.of(2023, 12, 17)

            val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
            val ytelserService = YtelserService(fixedDateProvider)

            val transformerteInntekter =
                ytelserService.beregnYtelser(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 2) },

                Executable { assertTrue(transformerteInntekter[0].inntektRapportering == Inntektsrapportering.DAGPENGER) },
                Executable { assertTrue(transformerteInntekter[0].visningsnavn == Inntektsrapportering.DAGPENGER.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(50000)) },
                Executable { assertEquals(YearMonth.of(2021, 1), transformerteInntekter[0].periode.fom) },
                Executable { assertEquals(YearMonth.of(2021, 12), transformerteInntekter[0].periode.til) },
                Executable { assertTrue(transformerteInntekter[0].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 2) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp } == BigDecimal.valueOf(50000)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].kode == "dagpengerTilFiskerSomBareHarHyre") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].visningsnavn == "Dagpenger til fisker som bare har hyre") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[0].beløp == BigDecimal.valueOf(30000)) },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[1].kode == "dagpengerVedArbeidsloeshet") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[1].visningsnavn == "Dagpenger ved arbeidsløshet") },
                Executable { assertTrue(transformerteInntekter[0].inntektPostListe[1].beløp == BigDecimal.valueOf(20000)) },

                Executable { assertTrue(transformerteInntekter[1].inntektRapportering == Inntektsrapportering.DAGPENGER) },
                Executable { assertTrue(transformerteInntekter[1].visningsnavn == Inntektsrapportering.DAGPENGER.visningsnavn.intern) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(1000)) },
                Executable { assertEquals(YearMonth.of(2022, 1), transformerteInntekter[1].periode.fom) },
                Executable { assertEquals(YearMonth.of(2022, 12), transformerteInntekter[1].periode.til) },
                Executable { assertTrue(transformerteInntekter[1].gjelderBarnPersonId == "") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 1) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp } == BigDecimal.valueOf(1000)) },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].kode == "dagpengerVedArbeidsloeshet") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].visningsnavn == "Dagpenger ved arbeidsløshet") },
                Executable { assertTrue(transformerteInntekter[1].inntektPostListe[0].beløp == BigDecimal.valueOf(1000)) },
            )
        }

        @Test
        @Suppress("NonAsciiCharacters")
        fun `skal summere alle ytelser fra ainntekter`() {
            val filnavnYtelserDagpengerRequest = "src/test/resources/testfiler/eksempel_request_alle_ytelser.json"
            val inntektRequest = TestUtil.byggInntektRequest(filnavnYtelserDagpengerRequest)

            val dagensDato = LocalDate.of(2023, 12, 17)
            val ainntektHentetDato = LocalDate.of(2023, 12, 17)

            val fixedDateProvider: DateProvider = FixedDateProvider(dagensDato)
            val ytelserService = YtelserService(fixedDateProvider)

            val transformerteInntekter =
                ytelserService.beregnYtelser(inntektRequest.ainntektsposter, ainntektHentetDato)

            TestUtil.printJson(transformerteInntekter)

            assertAll(
                Executable { assertNotNull(transformerteInntekter) },

                Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
                Executable { assertTrue(transformerteInntekter.size == 8) },

                Executable { assertTrue(transformerteInntekter[0].inntektRapportering == Inntektsrapportering.AAP) },
                Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(1000)) },
                Executable { assertTrue(transformerteInntekter[1].inntektRapportering == Inntektsrapportering.DAGPENGER) },
                Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(2000)) },
                Executable { assertTrue(transformerteInntekter[2].inntektRapportering == Inntektsrapportering.FORELDREPENGER) },
                Executable { assertTrue(transformerteInntekter[2].sumInntekt == BigDecimal.valueOf(1000)) },
                Executable { assertTrue(transformerteInntekter[3].inntektRapportering == Inntektsrapportering.INTRODUKSJONSSTØNAD) },
                Executable { assertTrue(transformerteInntekter[3].sumInntekt == BigDecimal.valueOf(1000)) },
                Executable { assertTrue(transformerteInntekter[4].inntektRapportering == Inntektsrapportering.KVALIFISERINGSSTØNAD) },
                Executable { assertTrue(transformerteInntekter[4].sumInntekt == BigDecimal.valueOf(1000)) },
                Executable { assertTrue(transformerteInntekter[5].inntektRapportering == Inntektsrapportering.OVERGANGSSTØNAD) },
                Executable { assertTrue(transformerteInntekter[5].sumInntekt == BigDecimal.valueOf(3000)) },
                Executable { assertTrue(transformerteInntekter[6].inntektRapportering == Inntektsrapportering.PENSJON) },
                Executable { assertTrue(transformerteInntekter[6].sumInntekt == BigDecimal.valueOf(23000)) },
                Executable { assertTrue(transformerteInntekter[7].inntektRapportering == Inntektsrapportering.SYKEPENGER) },
                Executable { assertTrue(transformerteInntekter[7].sumInntekt == BigDecimal.valueOf(3000)) },

            )
        }
    }
}
