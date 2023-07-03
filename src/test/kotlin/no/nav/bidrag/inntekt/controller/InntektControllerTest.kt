package no.nav.bidrag.inntekt.controller

import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.BidragInntektTest.Companion.TEST_PROFILE
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.exception.RestExceptionHandler
import no.nav.bidrag.inntekt.service.InntektService
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
class InntektControllerTest(@Autowired val exceptionLogger: ExceptionLogger) {
    private val inntektService: InntektService = InntektService()
    private val inntektController: InntektController = InntektController(inntektService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(inntektController)
        .setControllerAdvice(RestExceptionHandler(exceptionLogger))
        .build()

    @Test
    fun `skal transformere inntekter`() {
        val transformerteInntekter = TestUtil.performRequest(
            mockMvc,
            HttpMethod.POST,
            InntektController.TRANSFORMER_INNTEKTER,
            null,
            String::class.java
        ) { isOk() }

        assertEquals(transformerteInntekter, "Dummy response")
    }
}
