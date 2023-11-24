package no.nav.bidrag.inntekt.service

import no.nav.bidrag.domene.enums.inntekt.Inntektsrapportering
import no.nav.bidrag.inntekt.StubUtils
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.util.DateProvider
import no.nav.bidrag.inntekt.util.FixedDateProvider
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.http.HttpStatus
import java.time.LocalDate

@DisplayName("InntektServiceTest")
class InntektServiceTest : AbstractServiceTest() {

    private final val fixedDateProvider: DateProvider = FixedDateProvider(LocalDate.of(2023, 9, 1))
    private final val ainntektService: AinntektService = AinntektService(fixedDateProvider)
    private final val kontantstøtteService: KontantstøtteService = KontantstøtteService()
    private final val utvidetBarnetrygdOgSmåbarnstilleggService: UtvidetBarnetrygdOgSmåbarnstilleggService =
        UtvidetBarnetrygdOgSmåbarnstilleggService()
    private final val skattegrunnlagService: SkattegrunnlagService = SkattegrunnlagService()

    private final val inntektService: InntektService =
        InntektService(
            ainntektService,
            skattegrunnlagService,
            kontantstøtteService,
            utvidetBarnetrygdOgSmåbarnstilleggService,
        )

    private final val filnavnKodeverkLoennsbeskrivelser = "src/test/resources/__files/respons_kodeverk_loennsbeskrivelser.json"
    private final val filnavnKodeverkSummertSkattegrunnlag = "src/test/resources/__files/respons_kodeverk_summert_skattegrunnlag.json"
    private final val filnavnEksempelRequest = "src/test/resources/testfiler/eksempel_request.json"

    private final val inntektRequest = TestUtil.byggInntektRequest(filnavnEksempelRequest)

    @Test
    fun `skal transformere inntekter`() {
        StubUtils.stubKodeverkSkattegrunnlag(TestUtil.byggKodeverkResponse(filnavnKodeverkSummertSkattegrunnlag))
        StubUtils.stubKodeverkLønnsbeskrivelse(TestUtil.byggKodeverkResponse(filnavnKodeverkLoennsbeskrivelser))
        val transformerteInntekterResponseDto = inntektService.transformerInntekter(inntektRequest)

        assertAll(
            Executable { assertNotNull(transformerteInntekterResponseDto) },

            Executable { assertTrue(transformerteInntekterResponseDto.summertÅrsinntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertÅrsinntektListe.size == 8) },
            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe.filter { it.inntektRapportering == Inntektsrapportering.AINNTEKT }.size == 2,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe.filter {
                        it.inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_3MND
                    }.size == 1,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe.filter {
                        it.inntektRapportering == Inntektsrapportering.AINNTEKT_BEREGNET_12MND
                    }.size == 1,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe.filter {
                        it.inntektRapportering == Inntektsrapportering.LIGNINGSINNTEKT
                    }.size == 2,
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe.filter {
                        it.inntektRapportering == Inntektsrapportering.KAPITALINNTEKT
                    }.size == 2,
                )
            },

            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe[0].inntektPostListe[0].kode == "overtidsgodtgjoerelse",
                )
            },
            Executable {
                assertTrue(
                    transformerteInntekterResponseDto.summertÅrsinntektListe[0].inntektPostListe[0].visningsnavn == "Overtidsgodtgjørelse",
                )
            },

            Executable { assertTrue(transformerteInntekterResponseDto.summertMånedsinntektListe.isNotEmpty()) },
            Executable { assertTrue(transformerteInntekterResponseDto.summertMånedsinntektListe.size == 20) },
        )
    }
}
