package cl.triskeledu.aprendoia.domain

/** Descripciones en español según códigos meteorológicos WMO usados por Open-Meteo. */
object WeatherDescription {
    fun fromCode(code: Int?): String = when (code) {
        0 -> "Despejado"
        1, 2 -> "Mayormente despejado"
        3 -> "Nublado"
        45, 48 -> "Niebla"
        51, 53, 55, 56, 57 -> "Llovizna"
        61, 63, 65, 66, 67 -> "Lluvia"
        71, 73, 75, 77 -> "Nieve"
        80, 81, 82 -> "Chubascos"
        85, 86 -> "Chubascos de nieve"
        95, 96, 99 -> "Tormenta"
        else -> "Condiciones no disponibles"
    }
}
