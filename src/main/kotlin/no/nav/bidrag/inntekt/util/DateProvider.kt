package no.nav.bidrag.inntekt.util

import java.time.LocalDate

fun interface DateProvider {
    fun getCurrentDate(): LocalDate
}

open class RealDateProvider : DateProvider {
    override fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }
}

open class FixedDateProvider(private val fixedDate: LocalDate) : DateProvider {
    override fun getCurrentDate(): LocalDate {
        return fixedDate
    }
}
