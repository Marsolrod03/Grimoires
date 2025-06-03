package com.grimoires.Grimoires.ui.models

import ads_mobile_sdk.db
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.domain.model.Spell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PlayableCharacterViewModel : ViewModel() {

    val db = Firebase.firestore

    private val _currentCharacter = MutableStateFlow<PlayableCharacter?>(null)
    val currentCharacter: StateFlow<PlayableCharacter?> = _currentCharacter

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userCharacters = MutableStateFlow<List<PlayableCharacter>>(emptyList())
    val userCharacters: StateFlow<List<PlayableCharacter>> = _userCharacters


    fun fetchCharactersForUser(userId: String) {
        db.collection("characters")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _userCharacters.value = snapshot.toObjects(PlayableCharacter::class.java)
                }
            }
    }
    fun loadCharacterById(characterId: String) {
        if (characterId.isEmpty()) return

        _isLoading.value = true
        _error.value = null

        FirebaseFirestore.getInstance().collection("characters").document(characterId).get()
            .addOnSuccessListener { document ->
                _currentCharacter.value =
                    document.toObject(PlayableCharacter::class.java)
                        ?.copy(characterId = document.id)
            }.addOnFailureListener { exception ->
                _error.value = "Error loading character: ${exception.message}"
            }.addOnCompleteListener {
                _isLoading.value = false
            }
    }

    fun loadCharactersForUser(userId: String) {
        _isLoading.value = true
        _error.value = null

        FirebaseFirestore.getInstance().collection("characters").whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                _userCharacters.value = result.documents.mapNotNull {
                    it.toObject(PlayableCharacter::class.java)?.copy(characterId = it.id)
                }
            }.addOnFailureListener { exception ->
                _error.value = "Error loading characters: ${exception.message}"
            }.addOnCompleteListener {
                _isLoading.value = false
            }
    }

    fun addCharacterToFirestore(
        character: PlayableCharacter,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("characters")
            .document(character.characterId)
            .set(character)
            .addOnSuccessListener {
                fetchCharactersForUser(character.userId)
                onSuccess(character.characterId)
            }
            .addOnFailureListener { onError() }
    }

    fun saveSelectedItems(characterId: String, itemIds: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val itemRefs = itemIds.map { db.collection("items").document(it) }

        db.collection("characters")
            .document(characterId)
            .set(mapOf("inventory" to itemRefs), SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Inventory saved successfully.")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to save inventory.", it)
            }
    }

    fun saveSelectedSpells(characterId: String, spellIds: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val spellRefs = spellIds.map { db.collection("spells").document(it) }

        db.collection("characters")
            .document(characterId)
            .set(mapOf("spells" to spellRefs), SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Spells saved successfully.")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to save spells.", it)
            }
    }

    fun deleteCharacter(characterId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("characters")
            .document(characterId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun loadItemsByIds(characterId: String, onResult: (List<Item>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("characters").document(characterId).get()
            .addOnSuccessListener { document ->
                val itemRefs = document.get("inventory") as? List<DocumentReference> ?: emptyList()
                if (itemRefs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                val itemIds = itemRefs.mapNotNull { it.id }

                db.collection("items")
                    .whereIn(FieldPath.documentId(), itemIds)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val items = snapshot.documents.mapNotNull { it.toObject(Item::class.java) }
                        onResult(items)
                    }
                    .addOnFailureListener {
                        Log.e("Firestore", "Error loading items: $it")
                        onResult(emptyList())
                    }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error loading character document: $it")
                onResult(emptyList())
            }
    }

    fun loadSpellsByIds(characterId: String, onResult: (List<Spell>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("characters").document(characterId).get()
            .addOnSuccessListener { document ->
                val spellRefs = document.get("spells") as? List<DocumentReference> ?: emptyList()
                if (spellRefs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                val spellIds = spellRefs.mapNotNull { it.id }

                db.collection("spells")
                    .whereIn(FieldPath.documentId(), spellIds)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val spells =
                            snapshot.documents.mapNotNull { it.toObject(Spell::class.java) }
                        onResult(spells)
                    }
                    .addOnFailureListener {
                        Log.e("Firestore", "Error loading spells: $it")
                        onResult(emptyList())
                    }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error loading character document: $it")
                onResult(emptyList())
            }
    }
}