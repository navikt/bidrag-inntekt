package no.nav.bidrag.inntekt

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.transport.behandling.grunnlag.reponse.SkattegrunnlagDto
import no.nav.bidrag.transport.behandling.grunnlag.reponse.SkattegrunnlagspostDto
import okhttp3.internal.immutableListOf
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
                skattegrunnlagListe = byggSkattegrunnlagListe()
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
                skattegrunnlagListe = byggSkattegrunnlagListe()
            ),
            SkattegrunnlagDto(
                personId = "12345678901",
                periodeFra = LocalDate.parse("2022-01-01"),
                periodeTil = LocalDate.parse("2023-01-01"),
                aktiv = true,
                brukFra = LocalDateTime.now(),
                brukTil = LocalDateTime.now(),
                hentetTidspunkt = LocalDateTime.now(),
                skattegrunnlagListe = byggSkattegrunnlagListe()
            )
        )

        private fun byggSkattegrunnlagListe() = immutableListOf(
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
