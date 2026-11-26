package com.duoc.notixnuevo.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Contrato pactado con Xano: las rutas protegidas reciben un Bearer token.
interface NotixApi {
    @POST("auth/signup")
    suspend fun registro(@Body datos: RegistroRequest): TokenResponse

    @POST("auth/login")
    suspend fun login(@Body datos: LoginRequest): TokenResponse

    @GET("auth/me")
    suspend fun perfil(@Header("Authorization") bearer: String): Usuario

    @GET("tramites")
    suspend fun tramites(@Header("Authorization") bearer: String): List<Tramite>

    @POST("tramites")
    suspend fun crear(
        @Header("Authorization") bearer: String,
        @Body datos: CrearTramiteRequest
    ): Tramite

    @PUT("tramites/{tramites_id}")
    suspend fun resolver(
        @Header("Authorization") bearer: String,
        @Path("tramites_id") id: Long,
        @Body datos: CambiarEstadoRequest
    ): Tramite

    @DELETE("tramites/{tramites_id}")
    suspend fun eliminar(
        @Header("Authorization") bearer: String,
        @Path("tramites_id") id: Long
    ): Response<ResponseBody>

    @GET("feriados")
    suspend fun feriados(): List<Feriado>
}
