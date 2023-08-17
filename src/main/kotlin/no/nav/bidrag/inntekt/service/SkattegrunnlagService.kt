package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.domain.enums.PlussMinus
import no.nav.bidrag.inntekt.consumer.kodeverk.KodeverkConsumer
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.inntekt.exception.RestResponse
import no.nav.bidrag.inntekt.exception.custom.UgyldigInputException
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagDto
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.SummertAarsinntekt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.math.BigDecimal
import java.time.Year
import java.time.YearMonth

@Service
class SkattegrunnlagService(
    private val kodeverkConsumer: KodeverkConsumer
) {


    fun beregnKaps(skattegrunnlagListe: List<SkattegrunnlagDto>, kodeverksverdier: GetKodeverkKoderBetydningerResponse?): List<SummertAarsinntekt> {
        val pathKapsfil = "/files/mapping_kaps.yaml"
        val mappingKaps = hentMapping(pathKapsfil)

        return beregnInntekt(skattegrunnlagListe, mappingKaps, InntektBeskrivelse.KAPITALINNTEKT, kodeverksverdier)
    }

    fun beregnLigs(skattegrunnlagListe: List<SkattegrunnlagDto>, kodeverksverdier: GetKodeverkKoderBetydningerResponse?): List<SummertAarsinntekt> {
        val pathLigsfil = "/files/mapping_ligs.yaml"
        val mappingLigs = hentMapping(pathLigsfil)

        return beregnInntekt(skattegrunnlagListe, mappingLigs, InntektBeskrivelse.LIGNINGSINNTEKT, kodeverksverdier)
    }


    private fun beregnInntekt(
        skattegrunnlagListe: List<SkattegrunnlagDto>,
        mapping: List<MappingPoster>,
        inntektBeskrivelse: InntektBeskrivelse,
        kodeverksverdier: GetKodeverkKoderBetydningerResponse?
    ): List<SummertAarsinntekt> {

//        val kodeverksverdier = hentKodeverksverdier()

        val summertÅrsinntektListe = mutableListOf<SummertAarsinntekt>()

        skattegrunnlagListe.forEach { skattegrunnlagÅr ->
            if (skattegrunnlagÅr.periodeTil != skattegrunnlagÅr.periodeFra.plusYears(1) ||
                skattegrunnlagÅr.periodeFra.dayOfMonth != 1 ||
                skattegrunnlagÅr.periodeFra.monthValue != 1
            ) {
                throw UgyldigInputException(
                    "Ugyldig input i skattegrunnlag.periodeFra, skattegrunnlag.periodeTil (må være januar til januar neste år): " +
                        "$skattegrunnlagÅr.periodeFra $skattegrunnlagÅr.periodeTil"
                )
            }

            val inntektPostListe = mutableListOf<InntektPost>()
            var sumInntekt = BigDecimal.ZERO
            skattegrunnlagÅr.skattegrunnlagListe.forEach { post ->
                val match = mapping.find { it.fulltNavnInntektspost == post.inntektType }
                if (match != null) {
                    if (match.plussMinus == PlussMinus.PLUSS) {
                        sumInntekt += post.belop
                    } else {
                        sumInntekt -= post.belop
                    }

                    inntektPostListe.add(
                        InntektPost(
                            kode = match.fulltNavnInntektspost,
                            visningsnavn = if (kodeverksverdier == null) match.fulltNavnInntektspost
                            else finnVisningsnavn(match.fulltNavnInntektspost, kodeverksverdier),
                            beløp = post.belop
                        )
                    )
                }
            }
            summertÅrsinntektListe.add(
                SummertAarsinntekt(
                    inntektBeskrivelse = inntektBeskrivelse,
                    visningsnavn = inntektBeskrivelse.toString(),
                    referanse = "",
                    sumInntekt = sumInntekt,
                    periodeFra = YearMonth.of(skattegrunnlagÅr.periodeFra.year, skattegrunnlagÅr.periodeFra.month),
                    periodeTil = YearMonth.of(skattegrunnlagÅr.periodeTil.year, skattegrunnlagÅr.periodeTil.month),
                    inntektPostListe = inntektPostListe
                )
            )
        }

        return summertÅrsinntektListe
    }

    private fun hentMapping(path: String): List<MappingPoster> {
        try {
            val objectMapper = ObjectMapper(YAMLFactory())
            objectMapper.findAndRegisterModules()
            val pathKapsfil = ClassPathResource(path).inputStream
            val mapping: Map<Post, List<PostKonfig>> = objectMapper.readValue(pathKapsfil)
            return mapping.flatMap { (post, postKonfigs) ->
                postKonfigs.map { postKonfig ->
                    MappingPoster(
                        post.fulltNavnInntektspost,
                        PlussMinus.valueOf(postKonfig.plussMinus),
                        postKonfig.sekkepost == "JA",
                        Year.parse(postKonfig.fom),
                        Year.parse(postKonfig.tom)
                    )
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Kunne ikke laste fil", e)
        }
    }

    private fun finnVisningsnavn(fulltNavnInntektspost: String, kodeverksverdier: GetKodeverkKoderBetydningerResponse): String {
        var visningsnavn = ""
        val bokmål = "nb"
        for ((fulltNavn, betydningListe) in kodeverksverdier.betydninger) {
            if (fulltNavn == fulltNavnInntektspost) {
                for (betydning in betydningListe) {
                    betydning.beskrivelser.let { beskrivelser ->
                        for ((spraak, beskrivelse) in beskrivelser) {
                            if (spraak == bokmål) {
                                visningsnavn = beskrivelse.term
                            }
                        }

                    }
                }
            }
        }
        return if (visningsnavn == "") {
            fulltNavnInntektspost
        } else visningsnavn
    }

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(SkattegrunnlagService::class.java)
    }
}

data class Post(val fulltNavnInntektspost: String)

data class PostKonfig(
    val plussMinus: String,
    val sekkepost: String,
    val fom: String,
    val tom: String
)

data class MappingPoster(
    val fulltNavnInntektspost: String,
    val plussMinus: PlussMinus,
    val sekkepost: Boolean,
    val fom: Year,
    val tom: Year
)
