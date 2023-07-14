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
import java.text.SimpleDateFormat
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

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal transformere årsinntekter`() {
        val objectMapper = ObjectMapper()
        objectMapper.registerKotlinModule()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val filnavn = "src/test/resources/testfiler/eksempel_request.json"
        val json = Files.readString(Paths.get(filnavn))

        val inntektRequest: TransformerInntekterRequestDto = objectMapper.readValue(json, TransformerInntekterRequestDto::class.java)
        val transformerteInntekter = ainntektService.beregnAarsinntekt(inntektRequest.ainntektListe)

        println(objectMapper.writeValueAsString(transformerteInntekter))

        assertAll(
            Executable { assertNotNull(transformerteInntekter) },

            Executable { assertTrue(transformerteInntekter.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekter.size == 5) },

            Executable { assertTrue(transformerteInntekter[0].inntektType == InntektType.AINNTEKT) },
            Executable { assertTrue(transformerteInntekter[0].periodeFra == YearMonth.of(2021, 1)) },
            Executable { assertTrue(transformerteInntekter[0].periodeTil == YearMonth.of(2021, 12)) },
            Executable { assertTrue(transformerteInntekter[0].sumInntekt == BigDecimal.valueOf(4000)) },
            Executable { assertTrue(transformerteInntekter[0].inntektPostListe.size == 1) },
            Executable { assertTrue(transformerteInntekter[0].inntektPostListe.sumOf { it.beløp.toInt() } == 4000) },

            Executable { assertTrue(transformerteInntekter[1].inntektType == InntektType.AINNTEKT) },
            Executable { assertTrue(transformerteInntekter[1].periodeFra == YearMonth.of(2022, 1)) },
            Executable { assertTrue(transformerteInntekter[1].periodeTil == YearMonth.of(2022, 12)) },
            Executable { assertTrue(transformerteInntekter[1].sumInntekt == BigDecimal.valueOf(446000)) },
            Executable { assertTrue(transformerteInntekter[1].inntektPostListe.size == 3) },
            Executable { assertTrue(transformerteInntekter[1].inntektPostListe.sumOf { it.beløp.toInt() } == 446000) },

            Executable { assertTrue(transformerteInntekter[2].inntektType == InntektType.AINNTEKT) },
            Executable { assertTrue(transformerteInntekter[2].periodeFra == YearMonth.of(2023, 1)) },
            Executable { assertTrue(transformerteInntekter[2].periodeTil == YearMonth.of(2023, 12)) },
            Executable { assertTrue(transformerteInntekter[2].sumInntekt == BigDecimal.valueOf(468000)) },
            Executable { assertTrue(transformerteInntekter[2].inntektPostListe.size == 4) },
            Executable { assertTrue(transformerteInntekter[2].inntektPostListe.sumOf { it.beløp.toInt() } == 468000) },

            Executable { assertTrue(transformerteInntekter[3].inntektType == InntektType.AINNTEKT_BEREGNET_12MND) },
            Executable { assertTrue(transformerteInntekter[3].periodeFra == YearMonth.of(2022, 7)) },
            Executable { assertTrue(transformerteInntekter[3].periodeTil == YearMonth.of(2023, 6)) },
            Executable { assertTrue(transformerteInntekter[3].sumInntekt == BigDecimal.valueOf(708000)) },
            Executable { assertTrue(transformerteInntekter[3].inntektPostListe.size == 3) },
            Executable { assertTrue(transformerteInntekter[3].inntektPostListe.sumOf { it.beløp.toInt() } == 708000) },

            Executable { assertTrue(transformerteInntekter[4].inntektType == InntektType.AINNTEKT_BEREGNET_3MND) },
            Executable { assertTrue(transformerteInntekter[4].periodeFra == YearMonth.of(2023, 4)) },
            Executable { assertTrue(transformerteInntekter[4].periodeTil == YearMonth.of(2023, 6)) },
            Executable { assertTrue(transformerteInntekter[4].sumInntekt == BigDecimal.valueOf(203000)) },
            Executable { assertTrue(transformerteInntekter[4].inntektPostListe.size == 3) },
            Executable { assertTrue(transformerteInntekter[4].inntektPostListe.sumOf { it.beløp.toInt() } == 203000) }
        )
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal transformere månedsinntekter`() {
        val objectMapper = ObjectMapper()
        objectMapper.registerKotlinModule()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val filnavn = "src/test/resources/testfiler/eksempel_request.json"
        val json = Files.readString(Paths.get(filnavn))

        val inntektRequest: TransformerInntekterRequestDto = objectMapper.readValue(json, TransformerInntekterRequestDto::class.java)
        val transformerteInntekter = ainntektService.beregnMaanedsinntekt(inntektRequest.ainntektListe)

        println(objectMapper.writeValueAsString(transformerteInntekter))

        assertAll(
            Executable { assertNotNull(transformerteInntekter) },
            Executable { assertTrue(transformerteInntekter.size == 20) },
            Executable { assertTrue(transformerteInntekter.filter { it.periode.year == 2021 }.sumOf { it.sumInntekt.toInt() } == 4000) },
            Executable { assertTrue(transformerteInntekter.filter { it.periode.year == 2022 }.sumOf { it.sumInntekt.toInt() } == 446000) },
            Executable { assertTrue(transformerteInntekter.filter { it.periode.year == 2023 }.sumOf { it.sumInntekt.toInt() } == 468000) }
        )
    }
}
