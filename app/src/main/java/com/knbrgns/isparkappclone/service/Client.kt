package com.knbrgns.isparkappclone.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {

    private const val BASE_URL = "https://192.168.1.50:7102"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: TestAPI = retrofit.create(TestAPI::class.java)

}