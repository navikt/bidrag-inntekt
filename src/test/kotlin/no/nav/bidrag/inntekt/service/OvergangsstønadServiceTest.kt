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
import java.time.LocalDate

@DisplayName("InntektServiceTest")
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
//            Executable { assertThat(beregnedeOvergangsstønader.size).isEqualTo(2) },
//
//            Executable { assertThat(beregnedeOvergangsstønader[0].periodeFra).isEqualTo(LocalDate.parse("2022-01-01")) },
//            Executable { assertThat(beregnedeOvergangsstønader[0].inntektType).isEqualTo(InntektType.OVERGANGSSTØNAD) },
//            Executable { assertThat(beregnedeOvergangsstønader[0].sumInntekt).isEqualTo(BigDecimal.valueOf(1700)) },
//            Executable { assertThat(beregnedeOvergangsstønader[0].overgangsstonadDtoListe.size).isEqualTo(4) },

        )
    }

}
