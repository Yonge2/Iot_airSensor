package com.example.iot_test

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit_builder {

    private const val baseUrl = "http://10.0.2.2:3553/IoTpj_server/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(Retrofit_interface::class.java)
}