package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domain.enums.InntektBeskrivelse
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.exception.custom.UgyldigInputException
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    private final val filnavnKodeverkSummertSkattegrunnlag = "src/test/resources/testfiler/respons_kodeverk_summert_skattegrunnlag.json"
    private final val kodeverkResponse = TestUtil.byggKodeverkResponse(filnavnKodeverkSummertSkattegrunnlag)

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal kaste UgyldigInputException ved feil periodeFra og PeriodeTil i input`() {
        assertThrows<UgyldigInputException> {
            val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDtoMedFeilPeriode()
            skattegrunnlagService.beregnSkattegrunnlag(
                skattegrunnlagDto,
                kodeverkResponse,
                InntektBeskrivelse.KAPITALINNTEKT
            )
        }
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Kapsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeKapsinntekter =
            skattegrunnlagService.beregnSkattegrunnlag(skattegrunnlagDto, kodeverkResponse, InntektBeskrivelse.KAPITALINNTEKT)

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

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].kode).isEqualTo("andelIFellesTapVedSalgAvAndelISDF") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].visningsnavn).isEqualTo("Andel i felles tap ved salg av andel i SDF") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(1000)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].kode).isEqualTo("andreFradragsberettigedeKostnader") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].visningsnavn).isEqualTo("Andre fradragsberettigede kostnader") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(500)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].kode).isEqualTo("annenSkattepliktigKapitalinntektFraAnnetFinansprodukt") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].visningsnavn).isEqualTo("Annen skattepliktig kapitalinntekt fra annet finansprodukt") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(1500)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].kode).isEqualTo("samledeOpptjenteRenterIUtenlandskeBanker") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].visningsnavn).isEqualTo("Samlede opptjente renter i utenlandske banker") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(1700)) }

        )
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Ligsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeLigsinntekter =
            skattegrunnlagService.beregnSkattegrunnlag(skattegrunnlagDto, kodeverkResponse, InntektBeskrivelse.LIGNINGSINNTEKT)

        assertAll(
            Executable { assertNotNull(beregnedeLigsinntekter) },
            Executable { assertThat(beregnedeLigsinntekter[0].periodeFra).isEqualTo(YearMonth.parse("2021-01")) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektBeskrivelse).isEqualTo(InntektBeskrivelse.LIGNINGSINNTEKT) },
            Executable { assertThat(beregnedeLigsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1000)) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].kode).isEqualTo("alderspensjonFraIPAOgIPS") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].visningsnavn).isEqualTo("Alderspensjon fra IPA og IPS") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(100)) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].kode).isEqualTo("annenArbeidsinntekt") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].visningsnavn).isEqualTo("Annen arbeidsinntekt") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(200)) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].kode).isEqualTo("annenPensjonFoerAlderspensjon") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].visningsnavn).isEqualTo("Annen pensjon før alderspensjon") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(300)) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].kode).isEqualTo("arbeidsavklaringspenger") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].visningsnavn).isEqualTo("Arbeidsavklaringspenger") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(400)) }
        )
    }
}
