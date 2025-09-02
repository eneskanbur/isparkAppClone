package com.knbrgns.isparkappclone.repository

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.knbrgns.isparkappclone.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseRepo(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    fun signUpWithEmailPassword(fullName: String,email: String, password: String) {
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

    suspend fun saveUser(user: User): Result<String> =
        withContext(Dispatchers.IO) {
        try {
            val existingUser = firestore.collection("user").document(user.uid).get().await()

            if (existingUser.exists()) {
                return@withContext Result.failure(Exception("User already exists"))
            }

            firestore.collection("user").document(user.uid).set(user).await()
            Result.success("User saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(uid: String): Result<User?> =
        withContext(Dispatchers.IO) {
        try {
            val document = firestore.collection("user").document(uid).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    // CodeSent durumu artık gerekli değil.
}