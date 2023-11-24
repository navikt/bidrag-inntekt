package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@EnableMockOAuth2Server
class SkattegrunnlagServiceTest {

    @Autowired
    private lateinit var skattegrunnlagService: SkattegrunnlagService

    private final val filnavnKodeverkSummertSkattegrunnlag = "src/test/resources/__files/respons_kodeverk_summert_skattegrunnlag.json"
    private final val kodeverkResponse = TestUtil.byggKodeverkResponse(filnavnKodeverkSummertSkattegrunnlag)

    @Test
    fun `skal returnere Kapsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeKapsinntekter =
            skattegrunnlagService.beregnSkattegrunnlag(skattegrunnlagDto, kodeverkResponse, Inntektsrapportering.KAPITALINNTEKT)

        assertAll(
            Executable { assertNotNull(beregnedeKapsinntekter) },
            Executable { assertThat(beregnedeKapsinntekter.size).isEqualTo(2) },

            Executable { assertThat(beregnedeKapsinntekter[0].periode.fom).isEqualTo(YearMonth.parse("2021-01")) },
            Executable { assertThat(beregnedeKapsinntekter[0].periode.til).isEqualTo(YearMonth.parse("2021-12")) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektRapportering).isEqualTo(Inntektsrapportering.KAPITALINNTEKT) },
            Executable { assertThat(beregnedeKapsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
            Executable { assertThat(beregnedeKapsinntekter[0].gjelderBarnPersonId).isEqualTo("") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeKapsinntekter[1].periode.fom).isEqualTo(YearMonth.parse("2022-01")) },
            Executable { assertThat(beregnedeKapsinntekter[1].periode.til).isEqualTo(YearMonth.parse("2022-12")) },
            Executable { assertThat(beregnedeKapsinntekter[1].inntektRapportering).isEqualTo(Inntektsrapportering.KAPITALINNTEKT) },
            Executable { assertThat(beregnedeKapsinntekter[1].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
            Executable { assertThat(beregnedeKapsinntekter[1].gjelderBarnPersonId).isEqualTo("") },
            Executable { assertThat(beregnedeKapsinntekter[1].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].kode).isEqualTo("andelIFellesTapVedSalgAvAndelISDF") },
            Executable {
                assertThat(
                    beregnedeKapsinntekter[0].inntektPostListe[0].visningsnavn,
                ).isEqualTo("Andel i felles tap ved salg av andel i SDF")
            },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(1000)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].kode).isEqualTo("andreFradragsberettigedeKostnader") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].visningsnavn).isEqualTo("Andre fradragsberettigede kostnader") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(500)) },

            Executable {
                assertThat(
                    beregnedeKapsinntekter[0].inntektPostListe[2].kode,
                ).isEqualTo("annenSkattepliktigKapitalinntektFraAnnetFinansprodukt")
            },
            Executable {
                assertThat(
                    beregnedeKapsinntekter[0].inntektPostListe[2].visningsnavn,
                ).isEqualTo("Annen skattepliktig kapitalinntekt fra annet finansprodukt")
            },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(1500)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].kode).isEqualTo("samledeOpptjenteRenterIUtenlandskeBanker") },
            Executable {
                assertThat(
                    beregnedeKapsinntekter[0].inntektPostListe[3].visningsnavn,
                ).isEqualTo("Samlede opptjente renter i utenlandske banker")
            },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(1700)) },

        )
    }

    @Test
    fun `skal returnere Ligsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeLigsinntekter =
            skattegrunnlagService.beregnSkattegrunnlag(skattegrunnlagDto, kodeverkResponse, Inntektsrapportering.LIGNINGSINNTEKT)

        assertAll(
            Executable { assertNotNull(beregnedeLigsinntekter) },
            Executable { assertThat(beregnedeLigsinntekter[0].periode.fom).isEqualTo(YearMonth.parse("2021-01")) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektRapportering).isEqualTo(Inntektsrapportering.LIGNINGSINNTEKT) },
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
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(400)) },
        )
    }
}
