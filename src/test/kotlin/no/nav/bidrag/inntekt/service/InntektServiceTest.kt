package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.BidragInntektTest
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
        val transformerteInntekter = inntektService.transformerInntekter(TransformerInntekterRequestDto())

        assertAll(
            Executable { assertNotNull(transformerteInntekter) },
            Executable { assertTrue(transformerteInntekter.versjon.isEmpty()) },
            Executable { assertTrue(transformerteInntekter.ligningsinntektListe.isEmpty()) },
            Executable { assertTrue(transformerteInntekter.kapitalinntektListe.isEmpty()) },
            Executable { assertTrue(transformerteInntekter.inntektListe.isEmpty()) }
        )
    }
}
