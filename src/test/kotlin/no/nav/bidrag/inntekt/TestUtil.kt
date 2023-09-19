package no.nav.bidrag.inntekt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
<<<<<<< HEAD
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagspostDto
import no.nav.bidrag.transport.behandling.inntekt.request.Overgangsstonad
=======
import no.nav.bidrag.transport.behandling.grunnlag.response.OvergangsstonadDto
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagspostDto
>>>>>>> main
import no.nav.bidrag.transport.behandling.inntekt.request.SkattegrunnlagForLigningsår
import no.nav.bidrag.transport.behandling.inntekt.request.TransformerInntekterRequest
import okhttp3.internal.immutableListOf
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.StatusResultMatchersDsl
import java.io.File
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate

class TestUtil {

    companion object {

        fun byggSkattegrunnlagDtoMedFeilPeriode() = immutableListOf(
            SkattegrunnlagForLigningsår(
                ligningsår = 2022,
                skattegrunnlagsposter = byggSkattegrunnlagPostListe()
            )
        )

        fun byggSkattegrunnlagDto() = immutableListOf(
            SkattegrunnlagForLigningsår(
                ligningsår = 2021,
                skattegrunnlagsposter = byggSkattegrunnlagPostListe()
            ),
            SkattegrunnlagForLigningsår(
                ligningsår = 2022,
                skattegrunnlagsposter = byggSkattegrunnlagPostListe()
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

        fun byggOvergangsstønad() = immutableListOf(
            Overgangsstonad(
                periodeFra = LocalDate.parse("2021-12-01"),
                periodeTil = LocalDate.parse("2022-01-01"),
                belop = 100
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-01-01"),
                periodeTil = LocalDate.parse("2022-02-01"),
                belop = 200
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-02-01"),
                periodeTil = LocalDate.parse("2022-03-01"),
                belop = 300
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-03-01"),
                periodeTil = LocalDate.parse("2022-04-01"),
                belop = 400
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-04-01"),
                periodeTil = LocalDate.parse("2022-05-01"),
                belop = 500
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-05-01"),
                periodeTil = LocalDate.parse("2022-06-01"),
                belop = 600
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-06-01"),
                periodeTil = LocalDate.parse("2022-07-01"),
                belop = 700
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-07-01"),
                periodeTil = LocalDate.parse("2022-08-01"),
                belop = 800
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-08-01"),
                periodeTil = LocalDate.parse("2022-09-01"),
                belop = 900
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-09-01"),
                periodeTil = LocalDate.parse("2022-10-01"),
                belop = 1000
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-10-01"),
                periodeTil = LocalDate.parse("2022-11-01"),
                belop = 1100
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-11-01"),
                periodeTil = LocalDate.parse("2022-12-01"),
                belop = 1200
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2022-12-01"),
                periodeTil = LocalDate.parse("2023-01-01"),
                belop = 1300
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2023-01-01"),
                periodeTil = LocalDate.parse("2023-02-01"),
                belop = 1400
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2023-02-01"),
                periodeTil = LocalDate.parse("2023-03-01"),
                belop = 1500
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2023-03-01"),
                periodeTil = LocalDate.parse("2023-04-01"),
                belop = 1600
            ),
            Overgangsstonad(
                periodeFra = LocalDate.parse("2023-04-01"),
                periodeTil = LocalDate.parse("2023-05-01"),
                belop = 1700
            )
        )

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
                characterEncoding = "UTF-8"
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
            }.andReturn()

            return when (responseType) {
                String::class.java -> mvcResult.response.contentAsString as Response
                else -> ObjectMapper().findAndRegisterModules()
                    .readValue(mvcResult.response.contentAsString, responseType)
            }
        }

        fun byggKodeverkResponse(filnavn: String): GetKodeverkKoderBetydningerResponse {
            val objectMapper = ObjectMapper()
            objectMapper.registerKotlinModule()
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")

            val file = File(filnavn)
            return objectMapper.readValue(file, GetKodeverkKoderBetydningerResponse::class.java)
        }

        fun byggInntektRequest(filnavn: String): TransformerInntekterRequest {
            val objectMapper = ObjectMapper()
            objectMapper.registerKotlinModule()
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")

            val file = File(filnavn)
            return objectMapper.readValue(file, TransformerInntekterRequest::class.java)
        }

        fun <T> printJson(json: List<T>) {
            val objectMapper = ObjectMapper()
            objectMapper.registerKotlinModule()
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")

            println(objectMapper.writeValueAsString(json))
        }
    }
}
