package com.duoc.notixnuevo.data

import retrofit2.http.GET
import retrofit2.http.Path

interface XanoFeriadosApi {
    @GET("feriados")
    suspend fun listar(): List<Feriado>
}

data class NagerFeriadoDto(
    val date: String,
    val localName: String,
    val name: String
)

interface NagerFeriadosApi {
    @GET("api/v3/NextPublicHolidays/{pais}")
    suspend fun proximos(@Path("pais") pais: String): List<NagerFeriadoDto>
}
