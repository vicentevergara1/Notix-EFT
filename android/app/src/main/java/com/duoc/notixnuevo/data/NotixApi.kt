package com.duoc.notixnuevo.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotixApi {
    @GET("usuarios")
    suspend fun usuarios(): List<Usuario>

    @GET("tramites")
    suspend fun tramites(): List<Tramite>

    @GET("feriados")
    suspend fun feriados(): List<Feriado>

    @POST("tramites")
    suspend fun crear(@Body datos: CrearTramite): Tramite

    @PUT("tramites/{tramites_id}")
    suspend fun actualizar(
        @Path("tramites_id") id: Long,
        @Body datos: ActualizarTramite
    ): Tramite

    @DELETE("tramites/{tramites_id}")
    suspend fun eliminar(@Path("tramites_id") id: Long): Response<ResponseBody>
}
