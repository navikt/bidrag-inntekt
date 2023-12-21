package no.nav.bidrag.inntekt.controller

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.BidragInntektTest.Companion.TEST_PROFILE
import no.nav.bidrag.inntekt.StubUtils
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.aop.RestExceptionHandler
import no.nav.bidrag.inntekt.service.AinntektService
import no.nav.bidrag.inntekt.service.InntektService
import no.nav.bidrag.inntekt.service.KontantstøtteService
import no.nav.bidrag.inntekt.service.SkattegrunnlagService
import no.nav.bidrag.inntekt.service.UtvidetBarnetrygdOgSmåbarnstilleggService
import no.nav.bidrag.inntekt.service.YtelserService
import no.nav.bidrag.transport.behandling.inntekt.response.TransformerInntekterResponse
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDate

@DisplayName("InntektControllerTest")
@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(classes = [BidragInntektTest::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableMockOAuth2Server
@AutoConfigureWireMock(port = 0)
class InntektControllerTest(
    @Autowired val exceptionLogger: ExceptionLogger,
) {

    private final val ainntektService: AinntektService = AinntektService()
    private final val skattegrunnlagService: SkattegrunnlagService = SkattegrunnlagService()
    private final val kontantstøtteService: KontantstøtteService = KontantstøtteService()
    private final val utvidetBarnetrygdOgSmåbarnstilleggService: UtvidetBarnetrygdOgSmåbarnstilleggService =
        UtvidetBarnetrygdOgSmåbarnstilleggService()
    private final val ytelserService: YtelserService = YtelserService()
    private final val inntektService: InntektService =
        InntektService(ainntektService, skattegrunnlagService, kontantstøtteService, utvidetBarnetrygdOgSmåbarnstilleggService, ytelserService)
    private final val inntektController: InntektController = InntektController(inntektService)

    private var mockMvc: MockMvc =
        MockMvcBuilders.standaloneSetup(inntektController).setControllerAdvice(RestExceptionHandler(exceptionLogger))
            .addFilter<StandaloneMockMvcBuilder>({ request: ServletRequest?, response: ServletResponse, chain: FilterChain ->
                response.characterEncoding = "UTF-8" // this is crucial
                chain.doFilter(request, response)
            }, "/*").build()

    @BeforeEach
    fun initKodeverk() {
        StubUtils.stubKodeverkSkattegrunnlag()
        StubUtils.stubKodeverkLønnsbeskrivelse()
        StubUtils.stubKodeverkYtelsesbeskrivelser()
        StubUtils.stubKodeverkPensjonsbeskrivelser()
        StubUtils.stubKodeverkNaeringsinntektsbeskrivelser()
    }

    @Test
    fun `skal transformere inntekter`() {
        val filnavnKodeverkLoennsbeskrivelser = "src/test/resources/__files/respons_kodeverk_loennsbeskrivelser.json"
        val filnavnKodeverkSummertSkattegrunnlag = "src/test/resources/__files/respons_kodeverk_summert_skattegrunnlag.json"
        val filnavnEksempelRequest = "src/test/resources/testfiler/eksempel_request.json"

        StubUtils.stubKodeverkSkattegrunnlag(TestUtil.byggKodeverkResponse(filnavnKodeverkSummertSkattegrunnlag))
        StubUtils.stubKodeverkLønnsbeskrivelse(TestUtil.byggKodeverkResponse(filnavnKodeverkLoennsbeskrivelser))

        val transformerteInntekter = TestUtil.performRequest(
            mockMvc,
            HttpMethod.POST,
            InntektController.TRANSFORMER_INNTEKTER,
            TestUtil.byggInntektRequest(filnavnEksempelRequest).copy(ainntektHentetDato = LocalDate.of(2023, 9, 1)),
            TransformerInntekterResponse::class.java,
        ) { isOk() }

        assertAll(
            Executable { assertNotNull(transformerteInntekter) },
            Executable { assertTrue(transformerteInntekter.versjon.isEmpty()) },

            Executable { assertTrue(transformerteInntekter.summertÅrsinntektListe.isNotEmpty()) },
            Executable { assertEquals(8, transformerteInntekter.summertÅrsinntektListe.size) },
            Executable {
                assertEquals(
                    2,
                    transformerteInntekter.summertÅrsinntektListe.filter { it.inntektRapportering == Inntektsrapportering.AINNTEKT }.size,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekter.summertÅrsinntektListe.filter {
                        it.inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_3MND
                    }.size == 1,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekter.summertÅrsinntektListe.filter {
                        it.inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_12MND
                    }.size == 1,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekter.summertÅrsinntektListe.filter { it.inntektRapportering == Inntektsrapportering.LIGNINGSINNTEKT }.size == 2,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekter.summertÅrsinntektListe.filter { it.inntektRapportering == Inntektsrapportering.KAPITALINNTEKT }.size == 2,
                )
            },

            Executable { assertTrue(transformerteInntekter.summertMånedsinntektListe.isNotEmpty()) },
            Executable { assertEquals(20, transformerteInntekter.summertMånedsinntektListe.size) },
            Executable {
                assertEquals(
                    4000,
                    transformerteInntekter.summertMånedsinntektListe.filter { it.gjelderÅrMåned.year == 2021 }
                        .sumOf { it.sumInntekt.toInt() },
                )
            },
            Executable {
                assertEquals(
                    446000,
                    transformerteInntekter.summertMånedsinntektListe.filter { it.gjelderÅrMåned.year == 2022 }
                        .sumOf { it.sumInntekt.toInt() },
                )
            },
            Executable {
                assertEquals(
                    468000,
                    transformerteInntekter.summertMånedsinntektListe.filter { it.gjelderÅrMåned.year == 2023 }
                        .sumOf { it.sumInntekt.toInt() },
                )
            },
        )
    }
}
