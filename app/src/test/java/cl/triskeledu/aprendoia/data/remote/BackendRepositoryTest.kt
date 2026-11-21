package cl.triskeledu.aprendoia.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BackendRepositoryTest {
    @Test fun urlNormalizesFinalSlash() {
        assertEquals("http://10.0.2.2:8080/", BackendRepository.normalize(" http://10.0.2.2:8080/ "))
    }
    @Test fun rejectsInvalidProtocol() {
        assertThrows(IllegalArgumentException::class.java) {
            BackendRepository.normalize("file:///etc/passwd")
        }
    }
}
