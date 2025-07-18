package com.knbrgns.isparkappclone.service

import com.knbrgns.isparkappclone.model.Campaign
import com.knbrgns.isparkappclone.model.LoginRequest
import com.knbrgns.isparkappclone.model.LoginResponse
import com.knbrgns.isparkappclone.model.News
import com.knbrgns.isparkappclone.model.Park
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface AuthAPI {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

interface ParkAPI{
    @GET("Park")
    suspend fun getParks(): Response<List<Park>>

    @GET("ParkDetay?id={parkID}")
    suspend fun getParkWithId(@Path("parkID") parkID: Int): Response<Park>
}

interface DataAPI {
    @GET("api/news")
    suspend fun getNews(): Response<List<News>>

    @GET("api/campaign")
    suspend fun getCampaigns(): Response<List<Campaign>>

}

