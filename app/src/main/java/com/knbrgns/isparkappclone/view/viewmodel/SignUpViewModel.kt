package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.knbrgns.isparkappclone.model.User
import com.knbrgns.isparkappclone.repository.AuthResult
import com.knbrgns.isparkappclone.repository.FirebaseRepo
import com.knbrgns.isparkappclone.repository.ParkRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseRepo(application,FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance())
    val authResult: StateFlow<AuthResult> = firebaseRepo.authResult

    fun signUpUser(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            firebaseRepo.signUpWithEmailPassword(fullName,email, password)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseRepo.getCurrentUser()
    }

    suspend fun saveUserToFirestore(user: User) {
        firebaseRepo.saveUser(user)
    }



}