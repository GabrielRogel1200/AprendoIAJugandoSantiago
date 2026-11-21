package cl.triskeledu.aprendoia.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** Servicio REST externo para mostrar condiciones meteorológicas de Santiago. */
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun currentWeather(
        @Query("latitude") latitude: Double = -33.4489,
        @Query("longitude") longitude: Double = -70.6693,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m",
        @Query("timezone") timezone: String = "America/Santiago"
    ): WeatherResponse
}

data class WeatherResponse(
    val current: CurrentConditions?
)

data class CurrentConditions(
    val time: String?,
    @SerializedName("temperature_2m") val temperatureCelsius: Double?,
    @SerializedName("relative_humidity_2m") val humidityPercent: Int?,
    @SerializedName("weather_code") val weatherCode: Int?,
    @SerializedName("wind_speed_10m") val windSpeedKmh: Double?
)
