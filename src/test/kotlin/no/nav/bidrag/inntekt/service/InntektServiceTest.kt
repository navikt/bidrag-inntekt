package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.aop.RestResponse
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.FixedDateProvider
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.HttpClientErrorException
import java.time.LocalDate

@DisplayName("InntektServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class InntektServiceTest {

    private final val fixedDateProvider: DateProvider = FixedDateProvider(LocalDate.of(2023, 9, 1))
    private final val ainntektService: AinntektService = AinntektService(fixedDateProvider)
    private final val overgangsstønadService: OvergangsstønadService = OvergangsstønadService(fixedDateProvider)
    private final val skattegrunnlagService: SkattegrunnlagService = SkattegrunnlagService()
    private final val kodeverkConsumer: KodeverkConsumer = Mockito.mock(KodeverkConsumer::class.java)

    private final val inntektService: InntektService = InntektService(ainntektService, skattegrunnlagService, overgangsstønadService, kodeverkConsumer)

    private final val filnavnKodeverkLoennsbeskrivelser = "src/test/resources/__files/respons_kodeverk_loennsbeskrivelser.json"
    private final val filnavnKodeverkSummertSkattegrunnlag = "src/test/resources/__files/respons_kodeverk_summert_skattegrunnlag.json"
    private final val filnavnEksempelRequest = "src/test/resources/testfiler/eksempel_request.json"

    private final val inntektRequest = TestUtil.byggInntektRequest(filnavnEksempelRequest)

    @Test
    fun `skal transformere inntekter`() {
        Mockito.`when`(kodeverkConsumer.hentKodeverksverdier("Loennsbeskrivelse"))
            .thenReturn(RestResponse.Success(TestUtil.byggKodeverkResponse(filnavnKodeverkLoennsbeskrivelser)))

        Mockito.`when`(kodeverkConsumer.hentKodeverksverdier("Summert skattegrunnlag"))
            .thenReturn(RestResponse.Success(TestUtil.byggKodeverkResponse(filnavnKodeverkSummertSkattegrunnlag)))

        val transformerteInntekterResponseDto = inntektService.transformerInntekter(inntektRequest)

        assertAll(
            Executable { assertNotNull(transformerteInntekterResponseDto) },

            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.size == 12) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.AINNTEKT }.size == 2) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.OVERGANGSSTØNAD }.size == 2) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_3MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_12MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.LIGNINGSINNTEKT }.size == 2) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.KAPITALINNTEKT }.size == 2) },

            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse") },

            Executable { assertTrue(transformerteInntekterResponseDto.summertMaanedsinntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertMaanedsinntektListe.size == 20) }
        )
    }

    @Test
    fun `skal transformere inntekter hvor kall til kodeverk feiler`() {
        Mockito.`when`(kodeverkConsumer.hentKodeverksverdier("Loennsbeskrivelse"))
            .thenReturn(RestResponse.Failure("Feil ved kall til kodeverk", HttpStatus.BAD_REQUEST, HttpClientErrorException(HttpStatus.BAD_REQUEST)))

        Mockito.`when`(kodeverkConsumer.hentKodeverksverdier("Summert skattegrunnlag"))
            .thenReturn(RestResponse.Failure("Feil ved kall til kodeverk", HttpStatus.BAD_REQUEST, HttpClientErrorException(HttpStatus.BAD_REQUEST)))

        val transformerteInntekterResponseDto = inntektService.transformerInntekter(inntektRequest)

        assertAll(
            Executable { assertNotNull(transformerteInntekterResponseDto) },

            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.size == 12) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.AINNTEKT }.size == 2) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_3MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.AINNTEKT_BEREGNET_12MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.OVERGANGSSTØNAD }.size == 2) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_3MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.OVERGANGSSTØNAD_BEREGNET_12MND }.size == 1) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.LIGNINGSINNTEKT }.size == 2) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe.filter { it.inntektBeskrivelse == InntektBeskrivelse.KAPITALINNTEKT }.size == 2) },

            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse") },
            Executable { assertTrue(transformerteInntekterResponseDto.summertAarsinntektListe[0].inntektPostListe[0].visningsnavn == "overtidsgodtgjoerelse") },

            Executable { assertTrue(transformerteInntekterResponseDto.summertMaanedsinntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertMaanedsinntektListe.size == 20) }
        )
    }
}
