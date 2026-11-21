package cl.triskeledu.aprendoia.data.remote

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** El servidor es configurable: 10.0.2.2 en emulador; IP LAN en teléfono físico. */
class BackendRepository(private val context: Context) {
    companion object {
        const val DEFAULT_URL = "http://10.0.2.2:8080/"
        private const val PREFS = "ep4_backend_settings"
        private const val KEY_URL = "base_url"

        fun savedUrl(context: Context): String = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL

        fun saveUrl(context: Context, address: String) {
            val normalized = normalize(address)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(KEY_URL, normalized).apply()
        }

        fun normalize(address: String): String {
            val trimmed = address.trim().trimEnd('/')
            require(trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                "La dirección debe empezar con http:// o https://"
            }
            val uri = try { java.net.URI(trimmed + "/") } catch (e: Exception) { null }
            require(uri != null && !uri.host.isNullOrBlank() && (uri.port == -1 || uri.port in 1..65535)) { "Dirección del servidor inválida" }
            return trimmed + "/"
        }
    }

    private fun api(): BackendApi {
        val base = normalize(savedUrl(context))
        return Retrofit.Builder().baseUrl(base).addConverterFactory(GsonConverterFactory.create())
            .build().create(BackendApi::class.java)
    }

    suspend fun listPoints() = api().getPoints()
    suspend fun addPoint(data: ServerPointInput) = api().createPoint(data)
    suspend fun changePoint(id: Long, data: ServerPointInput) = api().updatePoint(id, data)
    suspend fun removePoint(id: Long) {
        val response = api().deletePoint(id)
        if (!response.isSuccessful) throw IllegalStateException("Servidor HTTP ${response.code()}")
    }
}
