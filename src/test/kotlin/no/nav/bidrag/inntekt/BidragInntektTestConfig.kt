package no.nav.bidrag.inntekt

import com.nimbusds.jose.JOSEObjectType
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.inntekt.BidragInntektLocal.Companion.LOCAL_PROFILE
import no.nav.bidrag.inntekt.BidragInntektTest.Companion.TEST_PROFILE
import no.nav.bidrag.inntekt.service.DateProvider
import no.nav.bidrag.inntekt.service.FixedDateProvider
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.LocalDate

@Configuration
@OpenAPIDefinition(
    info = Info(title = "bidrag-inntekt", version = "v1"),
    security = [SecurityRequirement(name = "bearer-key")]
)
@Profile(TEST_PROFILE, LOCAL_PROFILE)
class BidragInntektTestConfig {

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    fun generateTestToken(): String {
        val iss = mockOAuth2Server.issuerUrl("aad")
        val newIssuer = iss.newBuilder().host("localhost").build()
        val token = mockOAuth2Server.issueToken(
            "aad",
            "aud-localhost",
            DefaultOAuth2TokenCallback(
                "aad",
                "aud-localhost",
                JOSEObjectType.JWT.type,
                listOf("aud-localhost"),
                mapOf("iss" to newIssuer.toString()),
                3600
            )
        )
        return "Bearer " + token.serialize()
    }

    @Bean
    fun fixedDateProvider(): DateProvider {
        return fixedDateProvider(LocalDate.now()) // Default fixed date for testing
    }

    fun fixedDateProvider(fixedDate: LocalDate): DateProvider {
        return FixedDateProvider(fixedDate)
    }
}
