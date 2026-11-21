package cl.triskeledu.aprendoia.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherDescriptionMoreTest {
    @Test fun drizzleCode() = assertEquals("Llovizna", WeatherDescription.fromCode(53))
    @Test fun fogCode() = assertEquals("Niebla", WeatherDescription.fromCode(48))
    @Test fun cloudCode() = assertEquals("Nublado", WeatherDescription.fromCode(3))
    @Test fun snowCode() = assertEquals("Nieve", WeatherDescription.fromCode(73))
    @Test fun showerCode() = assertEquals("Chubascos", WeatherDescription.fromCode(81))
    @Test fun stormCode() = assertEquals("Tormenta", WeatherDescription.fromCode(95))
    @Test fun missingCode() = assertEquals("Condiciones no disponibles", WeatherDescription.fromCode(null))
}
