package no.nav.bidrag.inntekt.consumer.kodeverk.api

import java.time.LocalDate

data class HentKodeverkRequest(
    val ekskluderUgyldige: Boolean = false,
    val kodeverksnavn: String = "Summert skattegrunnlag"
)

class GetKodeverkKoderBetydningerResponse {
    var betydninger: Map<String, List<Betydning>> = emptyMap()
}

data class Betydning(
    val gyldigFra: LocalDate,
    val gyldigTil: LocalDate,
    val beskrivelser: Map<String, Beskrivelse>
)

data class Beskrivelse(
    val term: String,
    val tekst: String? = null
)
