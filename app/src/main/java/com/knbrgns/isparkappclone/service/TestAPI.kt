package com.knbrgns.isparkappclone.service

import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.LoginRequest
import com.knbrgns.isparkappclone.model.LoginResponse
import com.knbrgns.isparkappclone.model.News
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TestAPI {

    @GET()
    suspend fun getNews(
        @Header("Authorization") token: String
    ): List<News>

    suspend fun getNewWithId(
        @Header("Authorization") token: String,
        @Path("id") newsId: Int
    ): News

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("api/campaign")
    suspend fun getCampaigns(
        @Header("Authorization") token: String
    ): List<Campaign>

    @GET("api/campaign/{id}")
    suspend fun getCampaignById(
        @Header("Authorization") token: String,
        @Path("id") campaignId: Int
    ): Campaign
}

