package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.grimoires.Grimoires.domain.model.Attributes
class StatsViewModel : ViewModel() {
    var attributes by mutableStateOf(Attributes())
        private set

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
}

    private fun Any?.toIntSafe(): Int {
        return when (this) {
            is Long -> this.toInt()
            is Double -> this.toInt()
            is Int -> this
            else -> 10
        }
    }


