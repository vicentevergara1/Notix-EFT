package com.duoc.notixnuevo.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCliente {
    val api: NotixApi by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.XANO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotixApi::class.java)
    }
}
