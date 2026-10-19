package cl.triskeledu.aprendoia.domain

import android.util.Patterns

/** Reglas de formulario separadas de la interfaz visual. */
object FormValidators {
    fun profile(name: String, email: String, phone: String): Map<String, String> = buildMap {
        if (name.trim().length < 2) put("name", "Ingresa un nombre de al menos 2 caracteres")
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            put("email", "Correo electrónico inválido")
        if (phone.isNotBlank() && !phone.trim().matches(Regex("""\+?[0-9]{8,15}""")))
            put("phone", "Teléfono inválido (8 a 15 dígitos)")
    }

    fun point(title: String, latitude: String, longitude: String, qr: String): Map<String, String> = buildMap {
        if (title.isBlank()) put("title", "Escribe el nombre del lugar")
        if (latitude.toDoubleOrNull()?.let { it in -90.0..90.0 } != true)
            put("latitude", "Latitud entre -90 y 90")
        if (longitude.toDoubleOrNull()?.let { it in -180.0..180.0 } != true)
            put("longitude", "Longitud entre -180 y 180")
        if (qr.isBlank()) put("qr", "Introduce un código QR")
    }

    fun video(title: String, topic: String, uri: String): Map<String, String> = buildMap {
        if (title.isBlank()) put("title", "Introduce un título")
        if (topic.isBlank()) put("topic", "Introduce el tema")
        if (!uri.trim().startsWith("https://", ignoreCase = true))
            put("uri", "Utiliza una URL HTTPS válida")
    }
}
