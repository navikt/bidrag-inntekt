package no.nav.bidrag.inntekt

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import no.nav.bidrag.inntekt.BidragInntektLocal.Companion.LOCAL_PROFILE
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.Rule
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
@EnableMockOAuth2Server
@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
@ActiveProfiles(LOCAL_PROFILE)
@ComponentScan(
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [BidragInntekt::class, BidragInntektTest::class]
        )
    ]
)
class BidragInntektLocal {

    companion object {
        const val LOCAL_PROFILE = "local"
    }
}

fun main(args: Array<String>) {
    val profile = if (args.isEmpty()) LOCAL_PROFILE else args[0]
    val app = SpringApplication(BidragInntektLocal::class.java)
    app.setAdditionalProfiles(profile, "lokal-nais-secrets")
    app.run(*args)
}

@Configuration
@Profile(LOCAL_PROFILE)
class LocalConfig {

    @Rule
    var wm = WireMockRule(
        WireMockConfiguration.options()
            .extensions(ResponseTemplateTransformer(false))
    )

    @Bean
    fun runWiremockServer() {
        var wms = WireMockServer(1234)
        wms.start()
        wms.stubFor(
            WireMock.get(WireMock.urlPathMatching(".*/kodeverk/Summert.*")).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.toString())
                    .withStatus(HttpStatus.OK.value())
                    .withBodyFile("respons_kodeverk_summert_skattegrunnlag.json")
            )
        )

        wms.stubFor(
            WireMock.get(WireMock.urlPathMatching(".*/kodeverk/Loennsbeskrivelse.*")).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.toString())
                    .withStatus(HttpStatus.OK.value())
                    .withBodyFile("respons_kodeverk_loennsbeskrivelser.json")
            )
        )
    }
}
