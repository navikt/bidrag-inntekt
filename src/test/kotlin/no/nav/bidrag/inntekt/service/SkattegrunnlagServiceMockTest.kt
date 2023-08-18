package no.nav.bidrag.inntekt.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@DisplayName("SkattegrunnlagServiceMockTest")
@ExtendWith(MockitoExtension::class)
class SkattegrunnlagServiceMockTest {

    @InjectMocks
    private lateinit var skattegrunnlagService: SkattegrunnlagService

    object MockitoHelper {

        // use this in place of captor.capture() if you are trying to capture an argument that is not nullable
        fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
        fun <T> any(type: Class<T>): T = Mockito.any(type)
        fun <T> any(): T = Mockito.any()
    }
}
