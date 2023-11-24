package no.nav.bidrag.inntekt.consumer

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.aop.RestResponse
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

@ExtendWith(MockitoExtension::class)
@DisplayName("KodeverkConsumerTest")
class KodeverkConsumerTest {

    @InjectMocks
    private val kodeverkConsumer: KodeverkConsumer? = null

    @Mock
    private val restTemplateMock: HttpHeaderRestTemplate? = null

    @Test
    fun `Sjekk at ok respons fra Kodeverk-consumer endepunkt mappes korrekt`() {
        val filnavnKodeverkLoennsbeskrivelser = "src/test/resources/__files/respons_kodeverk_loennsbeskrivelser.json"
        val url = "/api/v1/kodeverk/Loennsbeskrivelser/koder/betydninger?ekskluderUgyldige=true&spraak=nb"

        Mockito.`when`(
            restTemplateMock?.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(GetKodeverkKoderBetydningerResponse::class.java),
            ),
        ).thenReturn(ResponseEntity(TestUtil.byggKodeverkResponse(filnavnKodeverkLoennsbeskrivelser), HttpStatus.OK))

        when (val restResponseKodeverk = kodeverkConsumer!!.hentKodeverksverdier("Loennsbeskrivelser")) {
            is RestResponse.Success -> {
                val hentKodeverkResponse = restResponseKodeverk.body
                Assertions.assertAll(
                    Executable { assertNotNull(hentKodeverkResponse) },
                )
            }

            else -> {
                fail("Test returnerte med RestResponse.Failure, som ikke var forventet")
            }
        }
    }

    @Test
    fun `Sjekk at exception fra Kodeverk-consumer endepunkt håndteres korrekt`() {
        val url = "/api/v1/kodeverk/Loennsbeskrivelser/koder/betydninger?ekskluderUgyldige=true&spraak=nb"

        Mockito.`when`(
            restTemplateMock?.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(GetKodeverkKoderBetydningerResponse::class.java),
            ),
        ).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

        when (val restResponseKodeverk = kodeverkConsumer!!.hentKodeverksverdier("Loennsbeskrivelser")) {
            is RestResponse.Failure -> {
                Assertions.assertAll(
                    Executable { assertTrue(restResponseKodeverk.statusCode == HttpStatus.BAD_REQUEST) },
                    Executable { assertThat(restResponseKodeverk.restClientException).isInstanceOf(HttpClientErrorException::class.java) },
                )
            }

            else -> {
                org.assertj.core.api.Assertions.fail("Test returnerte med RestResponse.Success, som ikke var forventet")
            }
        }
    }
}
