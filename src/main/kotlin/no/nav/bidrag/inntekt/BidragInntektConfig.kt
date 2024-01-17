package no.nav.bidrag.inntekt

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.examples.Example
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.DefaultCorsFilter
import no.nav.bidrag.commons.web.UserMdcFilter
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

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
@Import(CorrelationIdFilter::class, UserMdcFilter::class, DefaultCorsFilter::class, InntektApi::class)
class BidragInntektConfig {
    @Bean
    fun openApiCustomiser(examples: Collection<OpenApiExample>): OpenApiCustomizer {
        return OpenApiCustomizer { openAPI ->
            examples.forEach { example ->
                openAPI.components.addExamples(example.name, example.example)
                examples.groupBy { Pair(it.path, it.method) }.entries.forEach {
                    openAPI.addExamplesToPath(it.key, it.value)
                }
            }
        }
    }

    fun OpenAPI.addExamplesToPath(operation: Pair<String, HttpMethod>, openApiExamples: List<OpenApiExample>) {
        val requestBody = paths[operation.first]?.getOperation(operation.second)
            ?.requestBody

        val jsonBody = requestBody?.content?.get(MediaType.APPLICATION_JSON_VALUE)

        openApiExamples
            .forEach {
                jsonBody?.addExamples(it.name, it.example)
            }
    }

    fun PathItem.getOperation(httpMethod: HttpMethod) = when (httpMethod) {
        HttpMethod.POST -> post
        HttpMethod.PUT -> put
        HttpMethod.PATCH -> patch
        HttpMethod.DELETE -> delete
        HttpMethod.OPTIONS -> options
        HttpMethod.GET -> get
        else -> null
    }
}

data class OpenApiExample(
    val example: Example,
    val method: HttpMethod,
    val path: String,
    val name: String = example.description,
)
