package com.knbrgns.isparkappclone.util

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val TOKEN_KEY = "auth_token"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}