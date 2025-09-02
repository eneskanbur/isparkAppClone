package com.knbrgns.isparkappclone.view.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.knbrgns.isparkappclone.model.User
import com.knbrgns.isparkappclone.repository.FirebaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseRepo(
        application, FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )
    private val _users = MutableLiveData<User>()
    val users: MutableLiveData<User> = _users

    fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = firebaseRepo.getUser(firebaseRepo.getCurrentUser()!!.uid).getOrNull()

                if (user != null) {
                    users.postValue(user)
                }

            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

}