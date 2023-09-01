package no.nav.bidrag.inntekt.service

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
