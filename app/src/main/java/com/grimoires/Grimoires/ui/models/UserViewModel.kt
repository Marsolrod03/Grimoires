package com.grimoires.Grimoires.ui.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var username by mutableStateOf("")
        private set

    var nickname by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(true)
        private set

    val uid: String
        get() = auth.currentUser?.uid ?: ""

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username") ?: ""
                    nickname = document.getString("nickname") ?: ""
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Log.e("UserViewModel", "Failed to fetch user data", it)
            }
    }
}
