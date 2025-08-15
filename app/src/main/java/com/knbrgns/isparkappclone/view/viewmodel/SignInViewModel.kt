package com.knbrgns.isparkappclone.view.viewmodel

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

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            authRepo.signInWithEmailPassword(email, password)
        }
    }

}