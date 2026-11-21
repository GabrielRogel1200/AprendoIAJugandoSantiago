package cl.triskeledu.aprendoia.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Repositorio para desacoplar la pantalla de Retrofit. */
class WeatherRepository(
    private val service: WeatherApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)
) {
    suspend fun currentInSantiago(): CurrentConditions =
        requireNotNull(service.currentWeather().current) { "La API no entregó condiciones actuales" }
}
