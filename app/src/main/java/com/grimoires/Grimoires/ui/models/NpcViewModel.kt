package com.grimoires.Grimoires.ui.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grimoires.Grimoires.domain.model.NonPlayableCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class NpcViewModel  : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    fun getCampaignNpcs(campaignId: String): Flow<List<NonPlayableCharacter>> = flow {
        try {
            val querySnapshot = db.collection("npcs")
                .whereEqualTo("campaignId", campaignId)
                .get()
                .await()

            val npcList = querySnapshot.documents.map { doc ->
                doc.toObject(NonPlayableCharacter::class.java)?.copy(characterId = doc.id) ?: NonPlayableCharacter()
            }
            emit(npcList)
        } catch (e: Exception) {
            Log.e("NpcViewModel", "Error getting NPCs", e)
            emit(emptyList())
        }
    }

    fun createNpc(
        npc: NonPlayableCharacter,
        campaignId: String,
        onSuccess: () -> Unit
    ) {
        val npcData = hashMapOf(
            "characterName" to npc.characterName,
            "characterClass" to npc.characterClass,
            "race" to npc.race,
            "alignment" to npc.alignment,
            "level" to npc.level,
            "masterId" to npc.masterId,
            "campaignId" to campaignId,
            "description" to npc.description
        )

        db.collection("npcs")
            .add(npcData)
            .addOnSuccessListener {
                Log.d("NpcViewModel", "NPC created successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("NpcViewModel", "Error creating NPC", e)
            }
    }

    fun getNpc(npcId: String): Flow<NonPlayableCharacter?> = flow {
        val document = db.collection("npcs").document(npcId).get().await()
        val npc = document.toObject(NonPlayableCharacter::class.java)
        emit(npc?.copy(characterId = document.id))
    }
}