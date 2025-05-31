package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
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
                            strength = (attrsMap["strength"] as? Long)?.toInt() ?: 10,
                            dexterity = (attrsMap["dexterity"] as? Long)?.toInt() ?: 10,
                            constitution = (attrsMap["constitution"] as? Long)?.toInt() ?: 10,
                            intelligence = (attrsMap["intelligence"] as? Long)?.toInt() ?: 10,
                            wisdom = (attrsMap["wisdom"] as? Long)?.toInt() ?: 10,
                            charisma = (attrsMap["charisma"] as? Long)?.toInt() ?: 10,
                        )
                        attributes = loadedAttributes
                    }
                }
            }
    }

    fun updateAttributes(newAttributes: Attributes) {
        attributes = newAttributes
    }
}
