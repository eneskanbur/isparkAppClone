package com.knbrgns.isparkappclone.service

import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.LoginRequest
import com.knbrgns.isparkappclone.model.LoginResponse
import com.knbrgns.isparkappclone.model.News
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface AuthAPI {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

interface DataAPI {
    @GET("api/news")
    suspend fun getNews(): Response<List<News>>

    @GET("api/campaign")
    suspend fun getCampaigns(): Response<List<Campaign>>

    interface TestAPI {

        @GET("api/news")
        suspend fun getNews(
            @Header("Authorization") token: String
        ): Response<List<News>>

        @GET("api/news/{id}")
        suspend fun getNewWithId(
            @Header("Authorization") token: String,
            @Path("id") newsId: Int
        ): Response<List<News>>

        @POST("api/auth/login")
        suspend fun login(
            @Body request: LoginRequest
        ): Response<LoginResponse>

        @GET("api/campaign")
        suspend fun getCampaigns(
            @Header("Authorization") token: String
        ): Response<List<Campaign>>

        @GET("api/campaign/{id}")
        suspend fun getCampaignById(
            @Header("Authorization") token: String,
            @Path("id") campaignId: Int
        ): Response<List<Campaign>>
    }
}

