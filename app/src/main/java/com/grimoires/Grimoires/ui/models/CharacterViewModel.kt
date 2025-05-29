package com.grimoires.Grimoires.ui.models

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.domain.model.Spell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayableCharacterViewModel : ViewModel()  {
    private val _characters = MutableStateFlow<List<PlayableCharacter>>(emptyList())
    val characters: StateFlow<List<PlayableCharacter>> = _characters

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        FirebaseFirestore.getInstance()
            .collection("characters")
            .get()
            .addOnSuccessListener { result ->
                val charactersList = result.documents.mapNotNull { it.toObject(PlayableCharacter::class.java)?.copy(characterId = it.id) }
                _characters.value = charactersList
            }
    }

    fun addCharacter(character: PlayableCharacter) {
        _characters.value += character

    }

    fun saveSelectedItems(characterId: String, selectedItems: List<Item>) {
        if (characterId.isNotEmpty()) {
            val itemIds = selectedItems.map { it.itemId }
            FirebaseFirestore.getInstance()
                .collection("characters")
                .document(characterId)
                .update("equipment", itemIds)
        }
    }


    fun saveSelectedSpells(characterId: String, selectedSpells: List<Spell>) {
        if (characterId.isNotEmpty()) {
            val spellIds = selectedSpells.map { it.spellId }
            FirebaseFirestore.getInstance()
                .collection("characters")
                .document(characterId)
                .update("spells", spellIds)
        }
    }

}