package com.grimoires.Grimoires.ui.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
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
    val items: StateFlow<List<Item>> = _items


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
                val list = result.mapNotNull { it.toObject(CharacterClass::class.java) }
                _classes.value = list
            }
    }

    private fun fetchRaces() {
        firestore.collection("classes")
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { it.toObject(Race::class.java) }
                _races.value = list
            }
    }

    private fun fetchItems() {
        firestore.collection("items")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.map { doc ->
                    doc.toObject(Item::class.java)?.copy(itemId = doc.id)
                }.filterNotNull()
                _items.value = list
                Log.d("CatalogViewModel", "Fetched ${list.size} items")
            }
            .addOnFailureListener {
                Log.e("CatalogViewModel", "Failed to fetch items", it)
            }
    }


    fun fetchSpells() {
        val firestore = FirebaseFirestore.getInstance()
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
            .addOnFailureListener {
            }
    }
}
