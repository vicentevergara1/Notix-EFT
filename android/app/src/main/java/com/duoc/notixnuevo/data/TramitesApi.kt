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

interface TramitesApi {
    @GET("api/tramites")
    suspend fun tramites(@Header("Authorization") bearer: String): List<Tramite>

    @POST("api/tramites")
    suspend fun crear(
        @Header("Authorization") bearer: String,
        @Body datos: CrearTramiteRequest
    ): Tramite

    @PUT("api/tramites/{id}")
    suspend fun resolver(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body datos: CambiarEstadoRequest
    ): Tramite

    @DELETE("api/tramites/{id}")
    suspend fun eliminar(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<ResponseBody>
}
