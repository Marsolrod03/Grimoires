package com.grimoires.Grimoires.ui.models

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class SignUpViewModel : ViewModel() {

    private val _username: MutableState<String> = mutableStateOf("")
    private val _nickname: MutableState<String> = mutableStateOf("")
    private val _email: MutableState<String> = mutableStateOf("")
    private val _password: MutableState<String> = mutableStateOf("")
    private val _acceptedTerms: MutableState<Boolean> = mutableStateOf(false)
    private val _state: MutableState<HandleState> = mutableStateOf(HandleState.Idle)


    val username: String get() = _username.value
    val nickname: String get() = _nickname.value
    val email: String get() = _email.value
    val password: String get() = _password.value
    val acceptedTerms: Boolean get() = _acceptedTerms.value
    var state: HandleState get() = _state.value; set(value) { _state.value = value }



    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun updateUsername(value: String) {
        _username.value = value.trim()
        resetErrorIfValid()
    }

    fun updateNickname(value: String) {
        _nickname.value = value.trim()
        resetErrorIfValid()
    }

    fun updateEmail(value: String) {
        _email.value = value.trim()
        resetErrorIfValid()
    }

    fun updatePassword(value: String) {
        _password.value = value.trim()
        resetErrorIfValid()
    }

    fun updateAcceptedTerms(value: Boolean) {
        _acceptedTerms.value = value
        resetErrorIfValid()
    }


    fun signUp() {
        if (!validateFields()) return

        state = HandleState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUserProfile()
                    user?.let {
                        saveUserToFirestore(it.uid)
                    }
                    state = HandleState.Success
                } else {
                    state = HandleState.Error(parseFirebaseError(task.exception))
                }
            }
    }


    private fun updateUserProfile() {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$username ($nickname)")
            .build()

        user?.updateProfile(profileUpdates)
    }

    private fun validateFields(): Boolean {
        return when {
            username.isBlank() -> {
                state = HandleState.Error("Username is required")
                false
            }
            nickname.isBlank() -> {
                state = HandleState.Error("Nickname is required")
                false
            }
            email.isBlank() -> {
                state = HandleState.Error("Email is required")
                false
            }
            password.isBlank() -> {
                state = HandleState.Error("Password is required")
                false
            }
            password.length < 6 -> {
                state = HandleState.Error("Password must be at least 6 characters")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                state = HandleState.Error("Invalid email format")
                false
            }
            !acceptedTerms -> {
                state = HandleState.Error("You must accept the terms")
                false
            }
            else -> true
        }
    }

    private fun parseFirebaseError(exception: Exception?): String {
        return when {
            exception?.message?.contains("email address is already in use") == true ->
                "Email already registered"
            exception?.message?.contains("invalid email") == true ->
                "Invalid email format"
            exception?.message?.contains("password is invalid") == true ->
                "Weak password"
            else -> "Registration failed: ${exception?.message ?: "Unknown error"}"
        }
    }

    private fun resetErrorIfValid() {
        if (state is HandleState.Error && fieldsAreValid()) {
            state = HandleState.Idle
        }
    }

    private fun fieldsAreValid(): Boolean {
        return username.isNotBlank() &&
                nickname.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 6 &&
                acceptedTerms
    }

    private fun saveUserToFirestore(uid: String) {
        val userData = hashMapOf(
            "uid" to uid,
            "username" to username,
            "nickname" to nickname,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User data added")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing user", e)
            }
    }

}


sealed class HandleState {
    object Idle : HandleState()
    object Loading : HandleState()
    object Success : HandleState()
    data class Error(val message: String) : HandleState()
}
