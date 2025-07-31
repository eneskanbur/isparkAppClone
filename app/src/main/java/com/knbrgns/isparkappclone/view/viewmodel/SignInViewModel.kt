package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.knbrgns.isparkappclone.repository.AuthResult
import com.knbrgns.isparkappclone.repository.FirebaseRepo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val authRepo = FirebaseRepo(FirebaseAuth.getInstance())
    val authResult: StateFlow<AuthResult> = authRepo.authResult

    fun startPhoneNumberVerification(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            authRepo.sendVerificationCode(phoneNumber, activity)
        }
    }

    fun verifySmsCode(code: String) {
        viewModelScope.launch {
            authRepo.signInWithVerificationCode(code)
        }
    }
}