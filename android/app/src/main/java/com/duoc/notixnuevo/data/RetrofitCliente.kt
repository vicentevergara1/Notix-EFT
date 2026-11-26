package com.duoc.notixnuevo.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCliente {
    private fun crear(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarios: AuthApi by lazy {
        crear(ApiConfig.USUARIOS_URL).create(AuthApi::class.java)
    }
    val tramites: TramitesApi by lazy {
        crear(ApiConfig.TRAMITES_URL).create(TramitesApi::class.java)
    }
    val feriadosXano: XanoFeriadosApi by lazy {
        crear(ApiConfig.XANO_URL).create(XanoFeriadosApi::class.java)
    }
    val feriadosNager: NagerFeriadosApi by lazy {
        crear(ApiConfig.NAGER_URL).create(NagerFeriadosApi::class.java)
    }
}
