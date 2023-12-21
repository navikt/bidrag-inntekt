package no.nav.bidrag.inntekt

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import jakarta.annotation.PostConstruct
import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.commons.service.KodeverkProvider
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.DefaultCorsFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.commons.web.UserMdcFilter
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope

const val LIVE_PROFILE = "live"

@Configuration
@OpenAPIDefinition(
    info = Info(title = "bidrag-inntekt", version = "v1"),
    security = [SecurityRequirement(name = "bearer-key")],
)
@EnableJwtTokenValidation
@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearer-key",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
)
@Import(CorrelationIdFilter::class, UserMdcFilter::class, DefaultCorsFilter::class)
class BidragInntektConfig(@Value("\${KODEVERK_URL}") val url: String) {

    @PostConstruct
    fun initKodeverk() {
        KodeverkProvider.initialiser(url)
    }

    @Bean
    fun exceptionLogger(): ExceptionLogger {
        return ExceptionLogger(BidragInntekt::class.java.simpleName)
    }

    @Bean
    @Scope("prototype")
    fun restTemplate(): HttpHeaderRestTemplate {
        val httpHeaderRestTemplate = HttpHeaderRestTemplate()
        httpHeaderRestTemplate.addHeaderGenerator(CorrelationIdFilter.CORRELATION_ID_HEADER) { CorrelationId.fetchCorrelationIdForThread() }
        return httpHeaderRestTemplate
    }
}

@Profile(LIVE_PROFILE)
@Configuration
class InitKodeverkCache {
    @PostConstruct
    fun initKodeverkCache() {
        KodeverkProvider.initialiserKodeverkCache()
    }
}
