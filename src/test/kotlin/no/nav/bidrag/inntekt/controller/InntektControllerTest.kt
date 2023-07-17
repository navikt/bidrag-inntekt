package no.nav.bidrag.inntekt.controller

import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.BidragInntektTest.Companion.TEST_PROFILE
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.dto.TransformerInntekterRequestDto
import no.nav.bidrag.inntekt.dto.TransformerInntekterResponseDto
import no.nav.bidrag.inntekt.exception.RestExceptionHandler
import no.nav.bidrag.inntekt.service.AinntektService
import no.nav.bidrag.inntekt.service.InntektService
import no.nav.bidrag.inntekt.service.OvergangsstønadService
import no.nav.bidrag.inntekt.service.SkattegrunnlagService
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@DisplayName("InntektControllerTest")
@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(classes = [BidragInntektTest::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableMockOAuth2Server
class InntektControllerTest(
    @Autowired val exceptionLogger: ExceptionLogger,
    @Autowired val ainntektService: AinntektService,
    @Autowired val skattegrunnlagService: SkattegrunnlagService,
    @Autowired val overgangsstønadService: OvergangsstønadService
) {

    private val inntektService: InntektService = InntektService(ainntektService, skattegrunnlagService, overgangsstønadService)
//    private val inntektService: InntektService = InntektService(ainntektService)
    private val inntektController: InntektController = InntektController(inntektService)
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(inntektController)
        .setControllerAdvice(RestExceptionHandler(exceptionLogger))
        .build()

    @Test
    fun `skal transformere inntekter`() {
        val transformerteInntekter = TestUtil.performRequest(
            mockMvc,
            HttpMethod.POST,
            InntektController.TRANSFORMER_INNTEKTER,
            TransformerInntekterRequestDto(),
            TransformerInntekterResponseDto::class.java
        ) { isOk() }

        assertAll(
            Executable { assertNotNull(transformerteInntekter) },
            Executable { assertTrue(transformerteInntekter.versjon.isEmpty()) },
            Executable { assertTrue(transformerteInntekter.summertAarsinntektListe.isEmpty()) },
            Executable { assertTrue(transformerteInntekter.summertMaanedsinntektListe.isEmpty()) }
//            Executable { assertTrue(transformerteInntekter.ligningsinntektListe.isEmpty()) },
//            Executable { assertTrue(transformerteInntekter.kapitalinntektListe.isEmpty()) },
//            Executable { assertTrue(transformerteInntekter.inntektListe.isEmpty()) }
        )
    }
}
