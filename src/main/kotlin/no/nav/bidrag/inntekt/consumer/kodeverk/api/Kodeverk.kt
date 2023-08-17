package no.nav.bidrag.inntekt.consumer.kodeverk.api

import java.time.LocalDate

class GetKodeverkKoderBetydningerResponse {
    var betydninger: Map<String, List<Betydning>> = emptyMap()
        set(betydninger) {
            field = LinkedHashMap(betydninger)
        }
}

data class Betydning(
    val gyldigFra: LocalDate,
    val gyldigTil: LocalDate,
    val beskrivelser: Map<String, Beskrivelse>
)

data class Beskrivelse(
    val tekst: String,
    val term: String
)
