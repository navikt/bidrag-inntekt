package no.nav.bidrag.inntekt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.bidrag.inntekt.dto.Inntekt
import no.nav.bidrag.inntekt.dto.Overgangsstønad
import no.nav.bidrag.inntekt.dto.PlussMinus
import no.nav.bidrag.inntekt.dto.SkattegrunnlagInntekt
import no.nav.bidrag.inntekt.dto.SkattegrunnlagInntektPost
import no.nav.bidrag.inntekt.exception.custom.UgyldigInputException
import no.nav.bidrag.transport.behandling.grunnlag.reponse.OvergangsstonadDto
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.math.BigDecimal
import java.time.Year

@Service
class OvergangsstønadService() {

    /*

   private fun beregnOvergangsstønad(overgangsstønadListe: List<OvergangsstonadDto>): List<Overgangsstønad> {

       val overgangsstønadResponseListe = mutableListOf<Overgangsstønad>()
       val overgangsstonadDtoListe = mutableListOf<OvergangsstonadDto>()

         overgangsstønadListe.sortedWith(compareBy({ it.periodeFra }, { it.periodeTil })).forEach { overgangsstonad ->

           val skattegrunnlagInntektPostListe = mutableListOf<SkattegrunnlagInntektPost>()
           var sumInntekt = BigDecimal.ZERO
           overgangsstonad.skattegrunnlagListe.forEach { post ->
               val match = mapping.find { it.post == post.inntektType }
               if (match != null) {
                   if (match.plussMinus == PlussMinus.PLUSS) {
                       sumInntekt += post.belop
                   } else {
                       sumInntekt -= post.belop
                   }

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
           overgangsstønadResponseListe.add(
               SkattegrunnlagInntekt(
                   inntektType = inntektType,
                   aar = overgangsstonad.periodeFra.year.toString(),
                   sumInntekt = sumInntekt,
                   inntektPostListe = skattegrunnlagInntektPostListe
               )
           )
       }

       return overgangsstønadResponseListe
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
   }*/
    }




