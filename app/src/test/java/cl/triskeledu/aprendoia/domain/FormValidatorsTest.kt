package cl.triskeledu.aprendoia.domain

import org.junit.Assert.*
import org.junit.Test

/** Tests JVM de reglas puras de formularios (no requieren dispositivo). */
class FormValidatorsTest {
    @Test fun pointValidHasNoErrors() {
        assertTrue(FormValidators.point("Plaza de Armas", "-33.4372", "-70.6506", "TRISKEL-PLAZA").isEmpty())
    }
    @Test fun pointRejectsMissingTitle() {
        assertTrue(FormValidators.point(" ", "-33.4", "-70.6", "QR").containsKey("title"))
    }
    @Test fun pointRejectsBadLatitude() {
        assertTrue(FormValidators.point("Lugar", "-91", "-70.6", "QR").containsKey("latitude"))
    }
    @Test fun pointRejectsNonNumericLatitude() {
        assertTrue(FormValidators.point("Lugar", "abc", "-70.6", "QR").containsKey("latitude"))
    }
    @Test fun pointAcceptsBoundaryLatitude() {
        assertFalse(FormValidators.point("Lugar", "90", "0", "QR").containsKey("latitude"))
    }
    @Test fun pointRejectsBadLongitude() {
        assertTrue(FormValidators.point("Lugar", "-33.4", "181", "QR").containsKey("longitude"))
    }
    @Test fun pointRejectsEmptyQr() {
        assertTrue(FormValidators.point("Lugar", "-33.4", "-70.6", " ").containsKey("qr"))
    }
    @Test fun videoValidHasNoErrors() {
        assertTrue(FormValidators.video("Presentación", "IA", "https://example.org/video").isEmpty())
    }
    @Test fun videoRejectsMissingTitle() {
        assertTrue(FormValidators.video(" ", "IA", "https://example.org").containsKey("title"))
    }
    @Test fun videoRejectsMissingTopic() {
        assertTrue(FormValidators.video("Video", " ", "https://example.org").containsKey("topic"))
    }
    @Test fun videoRejectsInsecureHttp() {
        assertTrue(FormValidators.video("Video", "IA", "http://example.org").containsKey("uri"))
    }
    @Test fun videoAcceptsUppercaseHttpsScheme() {
        assertFalse(FormValidators.video("Video", "IA", "HTTPS://example.org").containsKey("uri"))
    }
}
