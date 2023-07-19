package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.domain.enums.PlussMinus
import no.nav.bidrag.inntekt.exception.custom.UgyldigInputException
import no.nav.bidrag.transport.behandling.grunnlag.response.SkattegrunnlagDto
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.SummertAarsinntekt
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.math.BigDecimal
import java.time.Year
import java.time.YearMonth

@Service
class SkattegrunnlagService() {

    fun beregnKaps(skattegrunnlagListe: List<SkattegrunnlagDto>): List<SummertAarsinntekt> {
        val pathKapsfil = "/files/mapping_kaps.yaml"
        val mappingKaps = hentMapping(pathKapsfil)

        return beregnInntekt(skattegrunnlagListe, mappingKaps, InntektBeskrivelse.KAPITALINNTEKT)
    }

    fun beregnLigs(skattegrunnlagListe: List<SkattegrunnlagDto>): List<SummertAarsinntekt> {
        val pathLigsfil = "/files/mapping_ligs.yaml"
        val mappingLigs = hentMapping(pathLigsfil)

        return beregnInntekt(skattegrunnlagListe, mappingLigs, InntektBeskrivelse.LIGNINGSINNTEKT)
    }

    private fun beregnInntekt(
        skattegrunnlagListe: List<SkattegrunnlagDto>,
        mapping: List<MappingPoster>,
        InntektBeskrivelse: InntektBeskrivelse
    ): List<SummertAarsinntekt> {
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
                val match = mapping.find { it.post == post.inntektType }
                if (match != null) {
                    if (match.plussMinus == PlussMinus.PLUSS) {
                        sumInntekt += post.belop
                    } else {
                        sumInntekt -= post.belop
                    }

                    inntektPostListe.add(
                        InntektPost(
                            kode = match.post,
                            visningsnavn = "",
                            beløp = post.belop
                        )
                    )
                }
            }
            summertÅrsinntektListe.add(
                SummertAarsinntekt(
                    inntektBeskrivelse = InntektBeskrivelse,
                    visningsnavn = InntektBeskrivelse.toString(),
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
                        post.post,
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
}

data class Post(val post: String)

data class PostKonfig(
    val plussMinus: String,
    val sekkepost: String,
    val fom: String,
    val tom: String
)

data class MappingPoster(
    val post: String,
    val plussMinus: PlussMinus,
    val sekkepost: Boolean,
    val fom: Year,
    val tom: Year
)
