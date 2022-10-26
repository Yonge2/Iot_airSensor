package com.example.iot_test

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface Retrofit_interface {

    @GET("get_air_info/")
    fun Get_air_info(): Call<get_air_info>

}