package com.knbrgns.isparkappclone.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class FirebaseRepo(private val auth: FirebaseAuth) {

    private val _verificationId = MutableStateFlow<String?>(null)

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
            _authResult.value = AuthResult.Error(e.message ?: "Bilinmeyen bir hata oluştu.")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            _verificationId.value = verificationId
            _authResult.value = AuthResult.CodeSent
        }
    }

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _authResult.value = AuthResult.Loading
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithVerificationCode(code: String) {
        _authResult.value = AuthResult.Loading
        _verificationId.value?.let { verificationId ->
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } ?: run {
            _authResult.value = AuthResult.Error("Doğrulama kimliği bulunamadı.")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authResult.value = AuthResult.Success
                } else {
                    val errorMessage = task.exception?.message ?: "Giriş işlemi başarısız."
                    _authResult.value = AuthResult.Error(errorMessage)
                }
            }
    }
}

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object CodeSent : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}