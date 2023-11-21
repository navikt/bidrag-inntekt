package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.bidrag.domene.enums.diverse.PlussMinus
import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.domene.tid.ÅrMånedsperiode
import no.nav.bidrag.inntekt.consumer.kodeverk.api.GetKodeverkKoderBetydningerResponse
import no.nav.bidrag.transport.behandling.inntekt.request.SkattegrunnlagForLigningsår
import no.nav.bidrag.transport.behandling.inntekt.response.InntektPost
import no.nav.bidrag.transport.behandling.inntekt.response.SummertÅrsinntekt
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.math.BigDecimal
import java.time.Month
import java.time.Year
import java.time.YearMonth

@Service
class SkattegrunnlagService {

    fun beregnSkattegrunnlag(
        skattegrunnlagListe: List<SkattegrunnlagForLigningsår>,
        kodeverksverdier: GetKodeverkKoderBetydningerResponse?,
        inntektsrapportering: Inntektsrapportering,
    ): List<SummertÅrsinntekt> {
        return if (skattegrunnlagListe.isNotEmpty()) {
            val filnavn =
                if (inntektsrapportering == Inntektsrapportering.KAPITALINNTEKT) "/files/mapping_kaps.yaml" else "files/mapping_ligs.yaml"
            val mapping = hentMapping(filnavn)
            beregnInntekt(
                skattegrunnlagListe = skattegrunnlagListe,
                mapping = mapping,
                inntektRapportering = inntektsrapportering,
                kodeverksverdier = kodeverksverdier,
            )
        } else {
            emptyList()
        }
    }

    private fun beregnInntekt(
        skattegrunnlagListe: List<SkattegrunnlagForLigningsår>,
        mapping: List<MappingPoster>,
        inntektRapportering: Inntektsrapportering,
        kodeverksverdier: GetKodeverkKoderBetydningerResponse?,
    ): List<SummertÅrsinntekt> {
        val summertÅrsinntektListe = mutableListOf<SummertÅrsinntekt>()

        skattegrunnlagListe.forEach { skattegrunnlagForLigningsår ->
            val inntektPostListe = mutableListOf<InntektPost>()
            var sumInntekt = BigDecimal.ZERO
            skattegrunnlagForLigningsår.skattegrunnlagsposter.forEach { post ->
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
                            visningsnavn = if (kodeverksverdier == null) {
                                match.fulltNavnInntektspost
                            } else {
                                finnVisningsnavn(fulltNavnInntektspost = match.fulltNavnInntektspost, kodeverksverdier = kodeverksverdier)
                            },
                            beløp = post.belop,
                        ),
                    )
                }
            }
            summertÅrsinntektListe.add(
                SummertÅrsinntekt(
                    inntektRapportering = inntektRapportering,
                    visningsnavn = "${inntektRapportering.visningsnavn} ${skattegrunnlagForLigningsår.ligningsår}",
                    referanse = "",
                    sumInntekt = sumInntekt,
                    periode = ÅrMånedsperiode(
                        fom = YearMonth.of(skattegrunnlagForLigningsår.ligningsår, Month.JANUARY),
                        til = YearMonth.of(skattegrunnlagForLigningsår.ligningsår, Month.DECEMBER),
                    ),
                    inntektPostListe = inntektPostListe,
                ),
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
                        fulltNavnInntektspost = post.fulltNavnInntektspost,
                        plussMinus = PlussMinus.valueOf(postKonfig.plussMinus),
                        sekkepost = postKonfig.sekkepost == "JA",
                        fom = Year.parse(postKonfig.fom),
                        tom = Year.parse(postKonfig.tom),
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
        } else {
            visningsnavn
        }
    }
}

data class Post(
    val fulltNavnInntektspost: String,
)

data class PostKonfig(
    val plussMinus: String,
    val sekkepost: String,
    val fom: String,
    val tom: String,
)

data class MappingPoster(
    val fulltNavnInntektspost: String,
    val plussMinus: PlussMinus,
    val sekkepost: Boolean,
    val fom: Year,
    val tom: Year,
)
