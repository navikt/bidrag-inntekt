package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.dto.InntektType
import no.nav.bidrag.inntekt.dto.PlussMinus
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

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal kaste UgyldigInputException ved feil periodeFra og PeriodeTil i input`() {
        assertThrows<UgyldigInputException> {
            val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDtoMedFeilPeriode()
            skattegrunnlagService.beregnKaps(skattegrunnlagDto)
        }
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Kapsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeKapsinntekter = skattegrunnlagService.beregnKaps(skattegrunnlagDto)

        assertAll(
            Executable { assertNotNull(beregnedeKapsinntekter) },
            Executable { assertThat(beregnedeKapsinntekter.size).isEqualTo(2) },

            Executable { assertThat(beregnedeKapsinntekter[0].aar).isEqualTo("2021") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektType).isEqualTo(InntektType.KAPITALINNTEKT) },
            Executable { assertThat(beregnedeKapsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeKapsinntekter[1].aar).isEqualTo("2022") },
            Executable { assertThat(beregnedeKapsinntekter[1].inntektType).isEqualTo(InntektType.KAPITALINNTEKT) },
            Executable { assertThat(beregnedeKapsinntekter[1].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
            Executable { assertThat(beregnedeKapsinntekter[1].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].inntektPostNavn).isEqualTo("andelIFellesTapVedSalgAvAndelISDF") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].plussEllerMinus).isEqualTo(PlussMinus.MINUS) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(1000)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].inntektPostNavn).isEqualTo("andreFradragsberettigedeKostnader") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].plussEllerMinus).isEqualTo(PlussMinus.MINUS) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(500)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].inntektPostNavn).isEqualTo("annenSkattepliktigKapitalinntektFraAnnetFinansprodukt") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].plussEllerMinus).isEqualTo(PlussMinus.PLUSS) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(1500)) },

            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].inntektPostNavn).isEqualTo("samledeOpptjenteRenterIUtenlandskeBanker") },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].plussEllerMinus).isEqualTo(PlussMinus.PLUSS) },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].erSekkePost).isTrue() },
            Executable { assertThat(beregnedeKapsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(1700)) }

        )
    }

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Ligsinntekter`() {
        val skattegrunnlagDto = TestUtil.byggSkattegrunnlagDto()
        val beregnedeLigsinntekter = skattegrunnlagService.beregnLigs(skattegrunnlagDto)

        assertAll(
            Executable { assertNotNull(beregnedeLigsinntekter) },
            Executable { assertThat(beregnedeLigsinntekter[0].aar).isEqualTo("2021") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektType).isEqualTo(InntektType.LIGNINGSINNTEKT) },
            Executable { assertThat(beregnedeLigsinntekter[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1000)) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe.size).isEqualTo(4) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].inntektPostNavn).isEqualTo("alderspensjonFraIPAOgIPS") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].plussEllerMinus).isEqualTo(PlussMinus.PLUSS) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[0].beløp).isEqualTo(BigDecimal.valueOf(100)) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].inntektPostNavn).isEqualTo("annenArbeidsinntekt") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].plussEllerMinus).isEqualTo(PlussMinus.PLUSS) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[1].beløp).isEqualTo(BigDecimal.valueOf(200)) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].inntektPostNavn).isEqualTo("annenPensjonFoerAlderspensjon") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].plussEllerMinus).isEqualTo(PlussMinus.PLUSS) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[2].beløp).isEqualTo(BigDecimal.valueOf(300)) },

            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].inntektPostNavn).isEqualTo("arbeidsavklaringspenger") },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].plussEllerMinus).isEqualTo(PlussMinus.PLUSS) },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].erSekkePost).isFalse() },
            Executable { assertThat(beregnedeLigsinntekter[0].inntektPostListe[3].beløp).isEqualTo(BigDecimal.valueOf(400)) }

        )
    }
}
