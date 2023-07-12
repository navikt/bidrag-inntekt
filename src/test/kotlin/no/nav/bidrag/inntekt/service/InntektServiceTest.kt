package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
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
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@DisplayName("InntektServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class InntektServiceTest {

    @Autowired
    private lateinit var inntektService: InntektService

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal transformere inntekter`() {
        val objectMapper = ObjectMapper()
        objectMapper.registerKotlinModule()
        objectMapper.registerModule(JavaTimeModule())
        val filnavn = "src/test/resources/testfiler/eksempel_request.json"
        val json = Files.readString(Paths.get(filnavn))

        val inntektRequest: TransformerInntekterRequestDto = objectMapper.readValue(json, TransformerInntekterRequestDto::class.java)
        val transformerteInntekter = inntektService.transformerInntekter(inntektRequest)
        println(transformerteInntekter)

        assertAll(
            Executable { assertNotNull(transformerteInntekter) },
//            Executable { assertTrue(transformerteInntekter.versjon.isEmpty()) },
//            Executable { assertTrue(transformerteInntekter.ligningsinntektListe.isEmpty()) },
//            Executable { assertTrue(transformerteInntekter.kapitalinntektListe.isEmpty()) },

            Executable { assertTrue(transformerteInntekter.inntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekter.inntektListe.size == 5) },

            Executable { assertTrue(transformerteInntekter.inntektListe[0].inntektType == InntektType.AINNTEKT) },
            Executable { assertTrue(transformerteInntekter.inntektListe[0].periodeFra == LocalDate.parse("2021-01-01")) },
            Executable { assertNotNull(transformerteInntekter.inntektListe[0].periodeTil) },
            Executable { assertTrue(transformerteInntekter.inntektListe[0].periodeTil!! == LocalDate.parse("2022-01-01")) },
            Executable { assertTrue(transformerteInntekter.inntektListe[0].sumInntekt == BigDecimal.valueOf(10000)) },

            Executable { assertTrue(transformerteInntekter.inntektListe[1].inntektType == InntektType.AINNTEKT) },
            Executable { assertTrue(transformerteInntekter.inntektListe[1].periodeFra == LocalDate.parse("2022-01-01")) },
            Executable { assertNotNull(transformerteInntekter.inntektListe[1].periodeTil) },
            Executable { assertTrue(transformerteInntekter.inntektListe[1].periodeTil!! == LocalDate.parse("2023-01-01")) },
            Executable { assertTrue(transformerteInntekter.inntektListe[1].sumInntekt == BigDecimal.valueOf(440000)) },

            Executable { assertTrue(transformerteInntekter.inntektListe[2].inntektType == InntektType.AINNTEKT) },
            Executable { assertTrue(transformerteInntekter.inntektListe[2].periodeFra == LocalDate.parse("2023-01-01")) },
            Executable { assertNotNull(transformerteInntekter.inntektListe[2].periodeTil) },
            Executable { assertTrue(transformerteInntekter.inntektListe[2].periodeTil!! == LocalDate.now()) },
            Executable { assertTrue(transformerteInntekter.inntektListe[2].sumInntekt == BigDecimal.valueOf(468000)) },

            Executable { assertTrue(transformerteInntekter.inntektListe[3].inntektType == InntektType.AINNTEKT_BEREGNET_12MND) },
            Executable { assertTrue(transformerteInntekter.inntektListe[3].periodeFra == LocalDate.now().minusYears(1)) },
            Executable { assertNotNull(transformerteInntekter.inntektListe[3].periodeTil) },
            Executable { assertTrue(transformerteInntekter.inntektListe[3].periodeTil!! == LocalDate.now()) },
            Executable { assertTrue(transformerteInntekter.inntektListe[3].sumInntekt == BigDecimal.valueOf(743000)) },

            Executable { assertTrue(transformerteInntekter.inntektListe[4].inntektType == InntektType.AINNTEKT_BEREGNET_3MND) },
            Executable { assertTrue(transformerteInntekter.inntektListe[4].periodeFra == LocalDate.now().minusMonths(3)) },
            Executable { assertNotNull(transformerteInntekter.inntektListe[4].periodeTil) },
            Executable { assertTrue(transformerteInntekter.inntektListe[4].periodeTil!! == LocalDate.now()) },
            Executable { assertTrue(transformerteInntekter.inntektListe[4].sumInntekt == BigDecimal.valueOf(228000)) }
        )
    }
}
