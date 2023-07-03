package no.nav.bidrag.inntekt

import no.nav.bidrag.inntekt.BidragInntektLocal.Companion.LOCAL_PROFILE
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles

@SpringBootApplication
@EnableMockOAuth2Server
@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
@ActiveProfiles(LOCAL_PROFILE)
@ComponentScan(excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [BidragInntekt::class, BidragInntektTest::class])])
class BidragInntektLocal {
    companion object {
        const val LOCAL_PROFILE = "local"
    }
}
fun main(args: Array<String>) {
    val profile = if (args.isEmpty()) LOCAL_PROFILE else args[0]
    val app = SpringApplication(BidragInntektLocal::class.java)
    app.setAdditionalProfiles(profile)
    app.run(*args)
}
