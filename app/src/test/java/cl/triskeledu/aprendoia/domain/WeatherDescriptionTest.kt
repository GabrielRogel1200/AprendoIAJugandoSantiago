package cl.triskeledu.aprendoia.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherDescriptionTest {
    @Test fun clearSkyIsTranslated() {
        assertEquals("Despejado", WeatherDescription.fromCode(0))
    }

    @Test fun rainIsTranslated() {
        assertEquals("Lluvia", WeatherDescription.fromCode(63))
    }

    @Test fun unknownCodeHasSafeFallback() {
        assertEquals("Condiciones no disponibles", WeatherDescription.fromCode(999))
    }
}
