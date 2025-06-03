package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var characters = mutableStateOf<List<PlayableCharacter>>(emptyList())
        private set

    private var _nickname = mutableStateOf("")
    val nickname: String
        get() = _nickname.value

    private val _currentCharacterId = MutableStateFlow("")
    val currentCharacterId: StateFlow<String> = _currentCharacterId.asStateFlow()


    val uid: String?
        get() = auth.currentUser?.uid


    init {
        loadUserData()
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { userDoc ->
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


    fun setCurrentCharacter(characterId: String) {
        _currentCharacterId.value = characterId
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("currentCharacterId", characterId)
    }
}
