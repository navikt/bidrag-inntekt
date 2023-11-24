package no.nav.bidrag.inntekt.service

import no.nav.bidrag.inntekt.BidragInntektTest
import no.nav.bidrag.inntekt.StubUtils
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
@Suppress("NonAsciiCharacters")
@ActiveProfiles(BidragInntektTest.TEST_PROFILE)
@SpringBootTest(
    classes = [BidragInntektTest::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@EnableMockOAuth2Server
@AutoConfigureWireMock(port = 0)
abstract class AbstractServiceTest {
    @BeforeEach
    fun initKodeverk() {
        StubUtils.stubKodeverkSkattegrunnlag()
        StubUtils.stubKodeverkLønnsbeskrivelse()
    }
}
