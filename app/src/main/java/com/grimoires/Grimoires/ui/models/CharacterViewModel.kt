package com.grimoires.Grimoires.ui.models

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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

    fun loadSpellsByIds(characterId: String, onLoaded: (List<Spell>) -> Unit) {
        db.collection("characters").document(characterId)
            .get()
            .addOnSuccessListener { document ->
                val spellRefs = document.get("spells") as? List<DocumentReference> ?: emptyList()

                if (spellRefs.isEmpty()) {
                    onLoaded(emptyList())
                    return@addOnSuccessListener
                }

                val ids = spellRefs.map { it.id }
                db.collection("spells")
                    .whereIn(FieldPath.documentId(), ids)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val spellsList = querySnapshot.documents.mapNotNull { doc ->
                            doc.toObject(Spell::class.java)?.copy(spellId = doc.id)
                        }
                        onLoaded(spellsList)
                    }
            }
    }

    fun loadItemsByIds(characterId: String, onLoaded: (List<Item>) -> Unit) {
        db.collection("characters").document(characterId)
            .get()
            .addOnSuccessListener { document ->
                val itemRefs = document.get("inventory") as? List<DocumentReference> ?: emptyList()

                if (itemRefs.isEmpty()) {
                    onLoaded(emptyList())
                    return@addOnSuccessListener
                }

                val ids = itemRefs.map { it.id }
                db.collection("items")
                    .whereIn(FieldPath.documentId(), ids)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val itemsList = querySnapshot.documents.mapNotNull { doc ->
                            doc.toObject(Item::class.java)?.copy(itemId = doc.id)
                        }
                        onLoaded(itemsList)
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


    fun deleteCharacter(characterId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("characters")
            .document(characterId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun saveSelectedSpells(characterId: String, spellIds: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val spellRefs = spellIds.map { db.collection("spells").document(it) }

        db.collection("characters")
            .document(characterId)
            .update("spells", spellRefs)
    }

    fun saveSelectedItems(characterId: String, itemIds: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val itemRefs = itemIds.map { db.collection("items").document(it) }

        db.collection("characters")
            .document(characterId)
            .update("inventory", itemRefs)
    }
}
