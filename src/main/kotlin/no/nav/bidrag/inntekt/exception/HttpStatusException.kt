import org.springframework.http.HttpStatus

abstract class HttpStatusException(message: String) : RuntimeException(message) {
    abstract val status: HttpStatus
}
