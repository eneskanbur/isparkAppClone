package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.knbrgns.isparkappclone.repository.AuthResult
import com.knbrgns.isparkappclone.repository.FirebaseRepo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseRepo(application,FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance())
    val authResult: StateFlow<AuthResult> = firebaseRepo.authResult

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            firebaseRepo.signInWithEmailPassword(email, password)
        }
    }

}