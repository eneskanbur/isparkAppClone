package com.knbrgns.isparkappclone.service

import android.util.Log
import com.knbrgns.isparkappclone.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    companion object {
        private const val TAG = "ISPARK_FLOW"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()
        val endpoint = originalRequest.url.encodedPath

        val request = if (!token.isNullOrEmpty()) {
            Log.d(TAG, "🔐 TOKEN ADDED -> $endpoint (Bearer token attached)")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w(TAG, "⚠️ NO TOKEN -> $endpoint (Request without token)")
            originalRequest
        }

        return chain.proceed(request)
    }
}