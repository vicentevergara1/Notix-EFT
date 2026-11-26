package com.duoc.notixnuevo.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/signup")
    suspend fun registro(@Body datos: RegistroRequest): TokenResponse

    @POST("api/auth/login")
    suspend fun login(@Body datos: LoginRequest): TokenResponse

    @GET("api/auth/me")
    suspend fun perfil(@Header("Authorization") bearer: String): Usuario
}
