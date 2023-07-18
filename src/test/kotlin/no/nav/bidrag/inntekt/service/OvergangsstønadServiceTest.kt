package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.TestUtil
import no.nav.bidrag.inntekt.dto.InntektType
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

@DisplayName("OvergangsstønadServiceTest")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableMockOAuth2Server
class OvergangsstønadServiceTest {

    @Autowired
    private lateinit var overgangsstønadService: OvergangsstønadService

    @Test
    @Suppress("NonAsciiCharacters")
    fun `skal returnere Overgangsstønad`() {
        val overgangsstønadDto = TestUtil.byggOvergangsstonadDto()
        val beregnedeOvergangsstønader = overgangsstønadService.beregnOvergangsstønad(overgangsstønadDto)

        assertAll(
            Executable { assertNotNull(beregnedeOvergangsstønader) },
            Executable { assertThat(beregnedeOvergangsstønader.size).isEqualTo(5) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektType).isEqualTo(InntektType.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeFra).isEqualTo(YearMonth.parse("2021-12")) },
            Executable { assertThat(beregnedeOvergangsstønader[0].periodeTil).isEqualTo(YearMonth.parse("2022-01")) },
            Executable { assertThat(beregnedeOvergangsstønader[0].inntektPostListe.size).isEqualTo(1) },
            Executable { assertThat(beregnedeOvergangsstønader[0].sumInntekt).isEqualTo(BigDecimal.valueOf(100)) },

            Executable { assertThat(beregnedeOvergangsstønader[1].inntektType).isEqualTo(InntektType.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeFra).isEqualTo(YearMonth.parse("2022-01")) },
            Executable { assertThat(beregnedeOvergangsstønader[1].periodeTil).isEqualTo(YearMonth.parse("2023-01")) },
            Executable { assertThat(beregnedeOvergangsstønader[1].inntektPostListe.size).isEqualTo(12) },
            Executable { assertThat(beregnedeOvergangsstønader[1].sumInntekt).isEqualTo(BigDecimal.valueOf(9000)) },

            Executable { assertThat(beregnedeOvergangsstønader[2].inntektType).isEqualTo(InntektType.OVERGANGSSTØNAD) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeFra).isEqualTo(YearMonth.parse("2023-01")) },
            Executable { assertThat(beregnedeOvergangsstønader[2].periodeTil).isEqualTo(YearMonth.now()) },
            Executable { assertThat(beregnedeOvergangsstønader[2].inntektPostListe.size).isEqualTo(4) },
            Executable { assertThat(beregnedeOvergangsstønader[2].sumInntekt).isEqualTo(BigDecimal.valueOf(6200)) },

            Executable { assertThat(beregnedeOvergangsstønader[3].inntektType).isEqualTo(InntektType.OVERGANGSSTØNAD_BEREGNET_3MND) },
            Executable { assertThat(beregnedeOvergangsstønader[3].periodeFra).isEqualTo(YearMonth.parse("2023-02")) },
            Executable { assertThat(beregnedeOvergangsstønader[3].periodeTil).isNull() },
            Executable { assertThat(beregnedeOvergangsstønader[3].inntektPostListe.size).isEqualTo(3) },
            Executable { assertThat(beregnedeOvergangsstønader[3].sumInntekt).isEqualTo(BigDecimal.valueOf(19200)) },

            Executable { assertThat(beregnedeOvergangsstønader[4].inntektType).isEqualTo(InntektType.OVERGANGSSTØNAD_BEREGNET_12MND) },
            Executable { assertThat(beregnedeOvergangsstønader[4].periodeFra).isEqualTo(YearMonth.parse("2022-05")) },
            Executable { assertThat(beregnedeOvergangsstønader[4].periodeTil).isNull() },
            Executable { assertThat(beregnedeOvergangsstønader[4].inntektPostListe.size).isEqualTo(12) },
            Executable { assertThat(beregnedeOvergangsstønader[4].sumInntekt).isEqualTo(BigDecimal.valueOf(13800)) }

        )
    }
}
