package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {



    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var characters = mutableStateOf<List<PlayableCharacter>>(emptyList())
        private set

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _currentCharacterId = MutableStateFlow("")
    val currentCharacterId: StateFlow<String> = _currentCharacterId.asStateFlow()

    private val _uid = MutableStateFlow<String?>(null)
    val uid: StateFlow<String?> = _uid

    init {
        fetchCurrentUserUid()

        viewModelScope.launch {
            uid.collectLatest { currentUid ->
                if (!currentUid.isNullOrEmpty()) {
                    loadUserData(currentUid)
                    updateFcmToken(currentUid)
                }
            }
        }
    }

    fun fetchCurrentUserUid() {
        _uid.value = auth.currentUser?.uid
    }

    private fun loadUserData(uid: String) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { userDoc ->
                val nick = userDoc.getString("nickname") ?: ""
                _nickname.value = nick

                val characterIdFromUser = userDoc.getString("currentCharacterId") ?: ""
                println("Loaded currentCharacterId from user document: $characterIdFromUser")

                db.collection("characters")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val chars = snapshot.documents.mapNotNull { it.toObject(PlayableCharacter::class.java) }
                        characters.value = chars
                        println("Loaded characters count: ${chars.size}")

                        _currentCharacterId.value = characterIdFromUser.ifEmpty {
                            chars.firstOrNull()?.characterId ?: ""
                        }
                        println("Current character id set to: ${_currentCharacterId.value}")
                    }
            }
    }

    private fun updateFcmToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            db.collection("users").document(uid).update("fcmToken", token)
        }
    }
}
