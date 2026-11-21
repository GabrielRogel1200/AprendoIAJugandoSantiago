package cl.triskeledu.aprendoia.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Contrato de datos real del microservicio Spring Boot de la Etapa 02. */
data class ServerPoint(
    val id: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val qrPayload: String,
    val points: Int
)

data class ServerPointInput(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val qrPayload: String,
    val points: Int
)

interface BackendApi {
    @GET("api/v1/points") suspend fun getPoints(): List<ServerPoint>
    @POST("api/v1/points") suspend fun createPoint(@Body data: ServerPointInput): ServerPoint
    @PUT("api/v1/points/{id}") suspend fun updatePoint(@Path("id") id: Long, @Body data: ServerPointInput): ServerPoint
    @DELETE("api/v1/points/{id}") suspend fun deletePoint(@Path("id") id: Long): Response<Unit>
}
