package no.nav.bidrag.inntekt

import no.nav.bidrag.inntekt.BidragInntektTest.Companion.TEST_PROFILE
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles

@SpringBootApplication
@ActiveProfiles(TEST_PROFILE)
@ComponentScan(excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [BidragInntekt::class, BidragInntektLocal::class])])
class BidragInntektTest {

    companion object {
        const val TEST_PROFILE = "test"
    }
}

fun main(args: Array<String>) {
    val profile = if (args.isEmpty()) TEST_PROFILE else args[0]
    val app = SpringApplication(BidragInntektTest::class.java)
    app.setAdditionalProfiles(profile)
    app.run(*args)
}
