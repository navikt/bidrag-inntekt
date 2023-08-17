package no.nav.bidrag.inntekt.service

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.exception.custom.UgyldigInputException
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.YearMonth

@DisplayName("SkattegrunnlagServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class SkattegrunnlagServiceTest {

    @Autowired
    private lateinit var skattegrunnlagService: SkattegrunnlagService


    @Autowired
    private lateinit var kodeverkConsumerMock: KodeverkConsumer

    @Mock
    private val restTemplateMock: HttpHeaderRestTemplate? = null

    @Captor
    private lateinit var kodeverkCaptor: ArgumentCaptor<String>

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal kaste UgyldigInputException ved feil periodeFra og PeriodeTil i input`() {
        assertThrows<UgyldigInputException> {
            val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDtoMedFeilPeriode()
            skattegrunnlagService.beregnKaps(skattegrunnlagDto, TestUtil.byggKodeverkSkattegrunnlagResponse())
        }
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Kapsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeKapsinntekter = skattegrunnlagService.beregnKaps(skattegrunnlagDto, TestUtil.byggKodeverkSkattegrunnlagResponse())

        assertAll(
            Executable { assertNotNull(beregnedeKapsinntekter) },
            Executable { assertThat(beregnedeKapsinntekter.size).isEqualTo(2) },

            Executable { assertThat(beregnedeKapsinntekter[0].periodeFra).isEqualTo(YearMonth.parse("2021-01")) },
            Executable { assertThat(beregnedeKapsinntekter[0].periodeTil).isEqualTo(YearMonth.parse("2022-01")) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektBeskrivelse).isEqualTo(InntektBeskrivelse.KAPITALINNTEKT) },
            Executable { assertThat(beregnedeKapsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeKapsinntekter[1].periodeFra).isEqualTo(YearMonth.parse("2022-01")) },
            Executable { assertThat(beregnedeKapsinntekter[1].periodeTil).isEqualTo(YearMonth.parse("2023-01")) },
            Executable { assertThat(beregnedeKapsinntekter[1].inntektBeskrivelse).isEqualTo(InntektBeskrivelse.KAPITALINNTEKT) },
            Executable { assertThat(beregnedeKapsinntekter[1].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
            Executable { assertThat(beregnedeKapsinntekter[1].inntektPostListe.size).isEqualTo(4) },

//            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].inntektPostNavn).isEqualTo("andelIFellesTapVedSalgAvAndelISDF") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(1000)) },

//            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].inntektPostNavn).isEqualTo("andreFradragsberettigedeKostnader") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(500)) },

//            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].inntektPostNavn).isEqualTo("annenSkattepliktigKapitalinntektFraAnnetFinansprodukt") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(1500)) },

//            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].inntektPostNavn).isEqualTo("samledeOpptjenteRenterIUtenlandskeBanker") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(1700)) }

        )
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Ligsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeLigsinntekter = skattegrunnlagService.beregnLigs(skattegrunnlagDto, TestUtil.byggKodeverkSkattegrunnlagResponse())

        assertAll(
            Executable { assertNotNull(beregnedeLigsinntekter) },
            Executable { assertThat(beregnedeLigsinntekter[0].periodeFra).isEqualTo(YearMonth.parse("2021-01")) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektBeskrivelse).isEqualTo(InntektBeskrivelse.LIGNINGSINNTEKT) },
            Executable { assertThat(beregnedeLigsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1000)) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe.size).isEqualTo(4) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].inntektPostNavn).isEqualTo("alderspensjonFraIPAOgIPS") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(100)) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].inntektPostNavn).isEqualTo("annenArbeidsinntekt") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(200)) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].inntektPostNavn).isEqualTo("annenPensjonFoerAlderspensjon") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(300)) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].inntektPostNavn).isEqualTo("arbeidsavklaringspenger") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(400)) }

        )
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal mappe respons fra kodeverk og lage respons`() {

        Mockito.`when`(
            restTemplateMock?.exchange(
                eq("/api/v1/kodeverk/Summert%20skattegrunnlag/koder/betydninger?ekskluderUgyldige=true&spraak=nb"),
                eq(HttpMethod.GET),
                any(),
                any<Class<GetKodeverkKoderBetydningerResponse>>()
            )
        )
            .thenReturn(ResponseEntity(TestUtil.byggKodeverkSkattegrunnlagResponse(), HttpStatus.OK))

        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeLigsinntekter = skattegrunnlagService.beregnLigs(skattegrunnlagDto, TestUtil.byggKodeverkSkattegrunnlagResponse())

        assertAll(
            Executable { assertNotNull(beregnedeLigsinntekter) },
            Executable { assertThat(beregnedeLigsinntekter[0].periodeFra).isEqualTo(YearMonth.parse("2021-01")) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektBeskrivelse).isEqualTo(InntektBeskrivelse.LIGNINGSINNTEKT) },
            Executable { assertThat(beregnedeLigsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1000)) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe.size).isEqualTo(4) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].inntektPostNavn).isEqualTo("alderspensjonFraIPAOgIPS") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(100)) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].inntektPostNavn).isEqualTo("annenArbeidsinntekt") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(200)) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].inntektPostNavn).isEqualTo("annenPensjonFoerAlderspensjon") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(300)) },

//            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].inntektPostNavn).isEqualTo("arbeidsavklaringspenger") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(400)) }

        )
    }



}
