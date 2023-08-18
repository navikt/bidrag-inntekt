package no.nav.bidrag.inntekt

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.inntekt.consumer.kodeverk.api.Beskrivelse
import no.nav.bidrag.inntekt.consumer.kodeverk.api.Betydning
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.transport.behandling.grunnlag.response.OvergangsstonadDto
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagDto
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagspostDto
import okhttp3.internal.immutableListOf
import org.junit.jupiter.api.Assertions
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.StatusResultMatchersDsl
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class TestUtil {

    companion object {

        fun byggSkattegrunnlagDtoMedFeilPeriode() = immutableListOf(
            SkattegrunnlagDto(
                personId = "12345678901",
                periodeFra = LocalDate.parse("2021-01-01"),
                periodeTil = LocalDate.parse("2023-01-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                skattegrunnlagListe = byggSkattegrunnlagPostListe()
            )
        )

        fun byggSkattegrunnlagDto() = immutableListOf(
            SkattegrunnlagDto(
                personId = "12345678901",
                periodeFra = LocalDate.parse("2021-01-01"),
                periodeTil = LocalDate.parse("2022-01-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                skattegrunnlagListe = byggSkattegrunnlagPostListe()
            ),
            SkattegrunnlagDto(
                personId = "12345678901",
                periodeFra = LocalDate.parse("2022-01-01"),
                periodeTil = LocalDate.parse("2023-01-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                skattegrunnlagListe = byggSkattegrunnlagPostListe()
            )
        )

        private fun byggSkattegrunnlagPostListe() = immutableListOf(
            // KAPS
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "andelIFellesTapVedSalgAvAndelISDF", // KAPS, MINUS, NEI
                belop = BigDecimal.valueOf(1000)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "andreFradragsberettigedeKostnader", // KAPS, MINUS, NEI
                belop = BigDecimal.valueOf(500)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "annenSkattepliktigKapitalinntektFraAnnetFinansprodukt", // KAPS, PLUSS, NEI
                belop = BigDecimal.valueOf(1500)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "samledeOpptjenteRenterIUtenlandskeBanker", // KAPS, PLUSS, JA
                belop = BigDecimal.valueOf(1700)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "ukjent",
                belop = BigDecimal.valueOf(100000)
            ),

            // LIGS
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "alderspensjonFraIPAOgIPS", // LIGS, PLUSS, NEI
                belop = BigDecimal.valueOf(100)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "ukjent", //
                belop = BigDecimal.valueOf(1700)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "annenArbeidsinntekt", // LIGS, PLUSS, NEI
                belop = BigDecimal.valueOf(200)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "annenPensjonFoerAlderspensjon", // LIGS, PLUSS, NEI
                belop = BigDecimal.valueOf(300)
            ),
            SkattegrunnlagspostDto(
                skattegrunnlagType = "Ordinær",
                inntektType = "arbeidsavklaringspenger", // LIGS, PLUSS, NEI
                belop = BigDecimal.valueOf(400)
            )

        )

        fun byggOvergangsstonadDto() = immutableListOf(
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2021-12-01"),
                periodeTil = LocalDate.parse("2022-01-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 100
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-01-01"),
                periodeTil = LocalDate.parse("2022-02-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 200
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-02-01"),
                periodeTil = LocalDate.parse("2022-03-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 300
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-03-01"),
                periodeTil = LocalDate.parse("2022-04-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 400
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-04-01"),
                periodeTil = LocalDate.parse("2022-05-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 500
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-05-01"),
                periodeTil = LocalDate.parse("2022-06-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 600
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-06-01"),
                periodeTil = LocalDate.parse("2022-07-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 700
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-07-01"),
                periodeTil = LocalDate.parse("2022-08-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 800
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-08-01"),
                periodeTil = LocalDate.parse("2022-09-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 900
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-09-01"),
                periodeTil = LocalDate.parse("2022-10-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1000
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-10-01"),
                periodeTil = LocalDate.parse("2022-11-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1100
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-11-01"),
                periodeTil = LocalDate.parse("2022-12-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1200
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2022-12-01"),
                periodeTil = LocalDate.parse("2023-01-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1300
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2023-01-01"),
                periodeTil = LocalDate.parse("2023-02-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1400
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2023-02-01"),
                periodeTil = LocalDate.parse("2023-03-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1500
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2023-03-01"),
                periodeTil = LocalDate.parse("2023-04-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1600
            ),
            OvergangsstonadDto(
                partPersonId = "12345678901",
                periodeFra = LocalDate.parse("2023-04-01"),
                periodeTil = LocalDate.parse("2023-05-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                belop = 1700
            )

        )

        fun byggKodeverkSkattegrunnlagResponse(): GetKodeverkKoderBetydningerResponse {
            val beskrivelse1 = Beskrivelse("Alderspensjon fra IPA og IPS", "Alderspensjon fra IPA og IPS")
            val beskrivelse2 = Beskrivelse("Annen arbeidsinntekt", "Annen arbeidsinntekt")
            val beskrivelse3 = Beskrivelse("Annen pensjon før alderspensjon", "Annen pensjon før alderspensjon")
            val beskrivelse4 = Beskrivelse("Arbeidsavklaringspenger", "Arbeidsavklaringspenger")

            val betydning1 = Betydning(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                mapOf("nb" to beskrivelse1)
            )

            val betydning2 = Betydning(
                LocalDate.of(2023, 8, 17),
                LocalDate.of(2024, 8, 17),
                mapOf("nb" to beskrivelse2)
            )

            val betydning3 = Betydning(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                mapOf("nb" to beskrivelse3)
            )

            val betydning4 = Betydning(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                mapOf("nb" to beskrivelse4)
            )

            val response = GetKodeverkKoderBetydningerResponse()
            response.betydninger = mapOf(
                ("alderspensjonFraIPAOgIPS" to listOf(betydning1)),
                ("annenArbeidsinntekt" to listOf(betydning2)),
                ("annenPensjonFoerAlderspensjon" to listOf(betydning3)),
                ("arbeidsavklaringspenger" to listOf(betydning4))
            )

            return response
        }

        // Les inn fil med request-data (json)
        fun lesFilOgByggResponse(filnavn: String): HttpEntity<String> {
            var json = ""
            try {
                json = this::class.java.getResource(filnavn)!!.readText()
            } catch (e: Exception) {
                Assertions.fail("Klarte ikke å lese fil: $filnavn")
            }
            return initHttpEntity(json)
        }

        private fun <T> initHttpEntity(body: T): HttpEntity<T> {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return HttpEntity(body, httpHeaders)
        }

        fun <Request, Response> performRequest(
            mockMvc: MockMvc,
            method: HttpMethod,
            url: String,
            input: Request?,
            responseType: Class<Response>,
            expectedStatus: StatusResultMatchersDsl.() -> Unit
        ): Response {
            val mockHttpServletRequestDsl: MockHttpServletRequestDsl.() -> Unit = {
                contentType = MediaType.APPLICATION_JSON
                if (input != null) {
                    content = when (input) {
                        is String -> input
                        else -> ObjectMapper().findAndRegisterModules().writeValueAsString(input)
                    }
                }
                accept = MediaType.APPLICATION_JSON
            }

            val mvcResult = when (method) {
                HttpMethod.POST -> mockMvc.post(url) { mockHttpServletRequestDsl() }
                HttpMethod.GET -> mockMvc.get(url) { mockHttpServletRequestDsl() }
                else -> throw NotImplementedError()
            }.andExpect {
                status { expectedStatus() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }.andReturn()

            return when (responseType) {
                String::class.java -> mvcResult.response.contentAsString as Response
                else -> ObjectMapper().findAndRegisterModules()
                    .readValue(mvcResult.response.contentAsString, responseType)
            }
        }
    }
}
