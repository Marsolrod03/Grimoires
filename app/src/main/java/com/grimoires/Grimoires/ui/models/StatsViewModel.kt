package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.grimoires.Grimoires.domain.model.Attributes
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.Spell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StatsViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    var attributes by mutableStateOf(Attributes())
        private set

    private val _characterItems = MutableStateFlow<List<Item>>(emptyList())
    val characterItems: StateFlow<List<Item>> = _characterItems

    private val _characterSpells = MutableStateFlow<List<Spell>>(emptyList())
    val characterSpells: StateFlow<List<Spell>> = _characterSpells

    fun loadAttributes(characterId: String) {
        FirebaseFirestore.getInstance().collection("characters").document(characterId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val attrsMap = document.get("attributes") as? Map<*, *>
                    if (attrsMap != null) {
                        val loadedAttributes = Attributes(
                            strength = attrsMap["strength"].toIntSafe(),
                            dexterity = attrsMap["dexterity"].toIntSafe(),
                            constitution = attrsMap["constitution"].toIntSafe(),
                            intelligence = attrsMap["intelligence"].toIntSafe(),
                            wisdom = attrsMap["wisdom"].toIntSafe(),
                            charisma = attrsMap["charisma"].toIntSafe(),
                        )
                        attributes = loadedAttributes
                    } else {
                        println("Attributes map is null")
                    }
                } else {
                    println("Document does not exist")
                }
            }
            .addOnFailureListener {
                println("Error loading document: $it")
            }
    }

    fun updateAttributes(characterId: String, newAttributes: Attributes) {
        val db = FirebaseFirestore.getInstance()
        val attributesMap = mapOf(
            "strength" to newAttributes.strength,
            "dexterity" to newAttributes.dexterity,
            "constitution" to newAttributes.constitution,
            "intelligence" to newAttributes.intelligence,
            "wisdom" to newAttributes.wisdom,
            "charisma" to newAttributes.charisma
        )

        db.collection("characters").document(characterId)
            .set(mapOf("attributes" to attributesMap), SetOptions.merge())
            .addOnSuccessListener {
                attributes = newAttributes
            }
            .addOnFailureListener { e ->
                println("Error updating attributes: $e")
            }
    }
    fun loadItemsForCharacter(characterId: String) {
        firestore.collection("characters").document(characterId).get()
            .addOnSuccessListener { document ->
                val itemRefs = document["inventory"] as? List<com.google.firebase.firestore.DocumentReference> ?: return@addOnSuccessListener

                itemRefs.forEach { ref ->
                    ref.get().addOnSuccessListener { itemDoc ->
                        val item = itemDoc.toObject(Item::class.java)
                        if (item != null) {
                            _characterItems.value = _characterItems.value + item
                        }
                    }
                }
            }
    }

    fun loadSpellsForCharacter(characterId: String) {
        firestore.collection("characters").document(characterId).get()
            .addOnSuccessListener { document ->
                val spellRefs = document["spells"] as? List<com.google.firebase.firestore.DocumentReference> ?: return@addOnSuccessListener

                spellRefs.forEach { ref ->
                    ref.get().addOnSuccessListener { spellDoc ->
                        val spell = spellDoc.toObject(Spell::class.java)
                        if (spell != null) {
                            _characterSpells.value = _characterSpells.value + spell
                        }
                    }
                }
            }
    }
}

    private fun Any?.toIntSafe(): Int {
        return when (this) {
            is Long -> this.toInt()
            is Double -> this.toInt()
            is Int -> this
            else -> 10
        }
    }


