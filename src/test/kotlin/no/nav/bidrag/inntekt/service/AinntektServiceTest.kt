package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var ainntektService: AinntektService

    private final val filnavnKodeverkLoennsbeskrivelser = "src/test/resources/testfiler/respons_kodeverk_loennsbeskrivelser.json"
    private final val filnavnEksempelRequest = "src/test/resources/testfiler/eksempel_request.json"

    private final val inntektRequest = TestUtil.byggInntektRequest(filnavnEksempelRequest)
    private final val kodeverkResponse = TestUtil.byggKodeverkResponse(filnavnKodeverkLoennsbeskrivelser)

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal transformere årsinntekter`() {
        val transformerteInntekter = ainntektService.beregnAarsinntekt(inntektRequest.ainntektListe, kodeverkResponse)

        TestUtil.printJson(transformerteInntekter)

        // Logikk for å beregne fra-/til-dato for 3-/12-mnd intervall. Kopiert fra AinntektService.bestemPeriode
        val dagensDato = LocalDate.now()
        val periodeTilIntervall = if (dagensDato.dayOfMonth > AinntektService.CUT_OFF_DATO) {
            YearMonth.of(dagensDato.year, dagensDato.month).minusMonths(1)
        } else {
            YearMonth.of(dagensDato.year, dagensDato.month).minusMonths(2)
        }
        val periodeFraIntervall3Mnd = periodeTilIntervall.minusMonths(2)
        val periodeFraIntervall12Mnd = periodeTilIntervall.minusMonths(11)

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
            Executable { assertTrue(transformerteInntekter[2].periodeFra == periodeFraIntervall12Mnd) },
            Executable { assertTrue(transformerteInntekter[2].periodeTil == periodeTilIntervall) },

            Executable { assertTrue(transformerteInntekter[3].inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND) },
            Executable { assertTrue(transformerteInntekter[3].visningsnavn == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND.visningsnavn) },
            Executable { assertTrue(transformerteInntekter[3].periodeFra == periodeFraIntervall3Mnd) },
            Executable { assertTrue(transformerteInntekter[3].periodeTil == periodeTilIntervall) }
        )
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal transformere månedsinntekter`() {
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
}
