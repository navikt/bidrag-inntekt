package no.nav.bidrag.inntekt

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
class BidragInntekt

fun main(args: Array<String>) {
    runApplication<BidragInntekt>(*args)
}
