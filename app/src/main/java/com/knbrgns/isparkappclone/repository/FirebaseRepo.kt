package com.knbrgns.isparkappclone.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirebaseRepo(private val auth: FirebaseAuth) {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    fun signUpWithEmailPassword(email: String, password: String) {
        _authResult.value = AuthResult.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authResult.value = AuthResult.Success
                } else {
                    val errorMessage = task.exception?.message ?: "Kayıt işlemi başarısız."
                    _authResult.value = AuthResult.Error(errorMessage)
                }
            }
    }

    fun signInWithEmailPassword(email: String, password: String) {
        _authResult.value = AuthResult.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authResult.value = AuthResult.Success
                } else {
                    val errorMessage = task.exception?.message ?: "Giriş işlemi başarısız."
                    _authResult.value = AuthResult.Error(errorMessage)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser


}

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    // CodeSent durumu artık gerekli değil.
}