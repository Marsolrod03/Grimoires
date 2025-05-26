package com.grimoires.Grimoires.ui.models


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest


class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _state = mutableStateOf<ProfileState>(ProfileState.Loading)
    val state: State<ProfileState> = _state

    init {
        verifyAuthentication()
    }

    private fun verifyAuthentication() {
        val user = auth.currentUser
        if (user == null) {
            _state.value = ProfileState.LoggedOut
        } else {
            loadUserData(user)
        }
    }

    private fun loadUserData(user: FirebaseUser) {
        _state.value = ProfileState.Success(
            userName = user.displayName ?: "Usuario",
            userEmail = user.email ?: "Sin email"
        )
    }

    fun logout() {
        auth.signOut()
        _state.value = ProfileState.LoggedOut
    }

    fun updateProfileName(newName: String) {
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(newName.trim())
                .build()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.let { user ->
                    _state.value = ProfileState.Success(
                        userName = user.displayName ?: "Usuario",
                        userEmail = user.email ?: "Sin email"
                    )
                }
            }
        }
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(
        val userName: String,
        val userEmail: String
    ) : ProfileState()

    data class Error(val message: String) : ProfileState()
    object LoggedOut : ProfileState()
}

