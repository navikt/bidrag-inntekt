package no.nav.bidrag.inntekt

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
class BidragInntekt

const val ISSUER = "aad"

fun main(args: Array<String>) {
    val profile = if (args.isEmpty()) LIVE_PROFILE else args[0]
    val app = SpringApplication(BidragInntekt::class.java)
    app.setAdditionalProfiles(profile)
    app.run(*args)
}
