package cl.triskeledu.aprendoia.data.remote

import org.junit.Assert.*
import org.junit.Test

class BackendUrlValidationTest {
    @Test fun normalizesHttpsUrl() {
        assertEquals("https://example.org:443/", BackendRepository.normalize(" https://example.org:443/// "))
    }
    @Test fun rejectsMissingProtocol() {
        assertThrows(IllegalArgumentException::class.java) { BackendRepository.normalize("localhost:8080") }
    }
    @Test fun rejectsEmptyHost() {
        assertThrows(IllegalArgumentException::class.java) { BackendRepository.normalize("http://") }
    }
    @Test fun rejectsUnsupportedScheme() {
        assertThrows(IllegalArgumentException::class.java) { BackendRepository.normalize("ftp://server.org/") }
    }
    @Test fun acceptsLocalLanHost() {
        assertEquals("http://192.168.1.20:8080/", BackendRepository.normalize("http://192.168.1.20:8080"))
    }
}
