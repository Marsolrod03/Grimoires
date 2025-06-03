package com.grimoires.Grimoires.ui.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grimoires.Grimoires.domain.model.CharacterClass
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.Race
import com.grimoires.Grimoires.domain.model.Spell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CatalogViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    private val _classes = MutableStateFlow<List<CharacterClass>>(emptyList())
    val classes: StateFlow<List<CharacterClass>> = _classes.asStateFlow()

    private val _races = MutableStateFlow<List<Race>>(emptyList())
    val races: StateFlow<List<Race>> = _races.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _spells = MutableStateFlow<List<Spell>>(emptyList())
    val spells: StateFlow<List<Spell>> = _spells.asStateFlow()

    init {
        fetchCharacterClasses()
        fetchRaces()
        fetchItems()
        fetchSpells()
    }

    private fun fetchCharacterClasses() {
        firestore.collection("charClass")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(CharacterClass::class.java)?.copy(classId = doc.id)
                }
                _classes.value = list
            }
            .addOnFailureListener { e ->
                Log.e("CatalogViewModel", "Failed to fetch character classes", e)
            }
    }

    private fun fetchRaces() {
        firestore.collection("classes")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Race::class.java)?.copy(raceId = doc.id)
                }
                _races.value = list
            }
            .addOnFailureListener { e ->
                Log.e("CatalogViewModel", "Failed to fetch races", e)
            }
    }

    private fun fetchItems() {
        firestore.collection("items")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Item::class.java)?.copy(itemId = doc.id)
                }
                _items.value = list
                Log.d("CatalogViewModel", "Fetched ${list.size} items")
            }
            .addOnFailureListener { e ->
                Log.e("CatalogViewModel", "Failed to fetch items", e)
            }
    }

    fun fetchSpells() {
        firestore.collection("spells")
            .get()
            .addOnSuccessListener { result ->
                val spellsList = result.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Spell::class.java)?.copy(spellId = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                _spells.value = spellsList
            }
            .addOnFailureListener { e ->
                Log.e("CatalogViewModel", "Failed to fetch spells", e)
            }
    }
}
