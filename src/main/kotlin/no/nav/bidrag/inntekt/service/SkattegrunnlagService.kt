package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.bidrag.behandling.felles.dto.grunnlag.SkattegrunnlagDto
import no.nav.bidrag.inntekt.dto.Inntekt
import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.PlussMinus
import no.nav.bidrag.inntekt.dto.SkattegrunnlagInntekt
import no.nav.bidrag.inntekt.dto.SkattegrunnlagInntektPost
import no.nav.bidrag.inntekt.exception.custom.UgyldigInputException
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.math.BigDecimal
import java.time.Year

@Service
class SkattegrunnlagService() {

    fun beregnKaps(skattegrunnlagListe: List<SkattegrunnlagDto>): List<SkattegrunnlagInntekt> {

        val pathKapsfil = "/files/mapping_kaps.yaml"
        val mappingKaps = hentMapping(pathKapsfil)

        return beregnInntekt(skattegrunnlagListe, mappingKaps, InntektType.KAPITALINNTEKT)

    }

    fun beregnLigs(skattegrunnlagListe: List<SkattegrunnlagDto>): List<SkattegrunnlagInntekt> {

        val pathLigsfil = "/files/mapping_ligs.yaml"
        val mappingLigs = hentMapping(pathLigsfil)

        return beregnInntekt(skattegrunnlagListe, mappingLigs, InntektType.LIGNINGSINNTEKT)

    }

    fun beregnInntekt(
        skattegrunnlagListe: List<SkattegrunnlagDto>,
        mapping: List<MappingPoster>,
        inntektType: InntektType
    ): List<SkattegrunnlagInntekt> {

        val skattegrunnlagInntektListe = mutableListOf<SkattegrunnlagInntekt>()

        skattegrunnlagListe.forEach { skattegrunnlagÅr ->
            if (skattegrunnlagÅr.periodeTil != skattegrunnlagÅr.periodeFra.plusYears(1)
                || skattegrunnlagÅr.periodeFra.dayOfMonth != 1
                || skattegrunnlagÅr.periodeFra.monthValue != 1) {
                throw UgyldigInputException("Ugyldig input i skattegrunnlagÅr.periodeFra, skattegrunnlagÅr.periodeTil (må være januar til januar neste år): " +
                    "$skattegrunnlagÅr.periodeFra $skattegrunnlagÅr.periodeTil")
            }

            val skattegrunnlagInntektPostListe = mutableListOf<SkattegrunnlagInntektPost>()
            var sumInntekt = BigDecimal.ZERO
            skattegrunnlagÅr.skattegrunnlagListe.forEach { post ->
                val match = mapping.find { it.post == post.inntektType }
                if (match != null) {
                    if (match.plussMinus == PlussMinus.PLUSS) {
                        sumInntekt += post.belop
                    } else sumInntekt -= post.belop

                    skattegrunnlagInntektPostListe.add(
                        SkattegrunnlagInntektPost(
                            inntektPostNavn = match.post,
                            plussEllerMinus = match.plussMinus,
                            erSekkePost = match.sekkepost,
                            beløp = post.belop
                        )
                    )
                }
            }
            skattegrunnlagInntektListe.add(
                SkattegrunnlagInntekt(
                    inntektType = inntektType,
                    aar = skattegrunnlagÅr.periodeFra.year.toString(),
                    sumInntekt = sumInntekt,
                    inntektPostListe = skattegrunnlagInntektPostListe
                )
            )

        }

        return skattegrunnlagInntektListe

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
    val tom: Year,
)
