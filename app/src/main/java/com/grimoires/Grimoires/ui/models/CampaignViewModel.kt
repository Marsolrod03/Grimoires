package com.grimoires.Grimoires.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.grimoires.Grimoires.domain.model.Campaign
import com.grimoires.Grimoires.domain.model.NonPlayableCharacter
import com.grimoires.Grimoires.domain.model.Note
import com.grimoires.Grimoires.domain.model.Participant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class CampaignViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _masteredCampaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val masteredCampaigns = _masteredCampaigns.asStateFlow()

    private val _playedCampaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val playedCampaigns = _playedCampaigns.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()


    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants = _participants.asStateFlow()

    private var campaignsListener: ListenerRegistration? = null
    private var playedCampaignsListener: ListenerRegistration? = null

    private val _npcs = MutableStateFlow<List<NonPlayableCharacter>>(emptyList())
    val npcs = _npcs.asStateFlow()


    fun getCampaign(campaignId: String): Flow<Campaign?> {
        return firestore.collection("campaigns").document(campaignId)
            .snapshots()
            .map { doc ->
                println("getCampaign → doc.exists: ${doc.exists()}, id: ${doc.id}, data: ${doc.data}")

                if (doc.exists()) {
                    try {
                        val accessCode = doc.getString("accessCode") ?: ""
                        val title = doc.getString("title") ?: ""
                        val description = doc.getString("description") ?: ""
                        val masterID = doc.getString("masterID") ?: ""
                        val playersRaw = doc.get("players") as? List<*> ?: emptyList<Any>()

                        val players = playersRaw.mapNotNull {
                            when (it) {
                                is DocumentReference -> it
                                is String -> firestore.document("users/$it")
                                else -> null
                            }
                        }

                        Campaign(
                            idCampaign = doc.id,
                            accessCode = accessCode,
                            title = title,
                            description = description,
                            masterID = masterID,
                            players = players
                        )
                    } catch (e: Exception) {
                        Log.e("CampaignViewModel", "Error al mapear campaña: ${e.message}")
                        null
                    }
                } else {
                    Log.w("CampaignViewModel", "Campaña no encontrada: $campaignId")
                    null
                }
            }
    }


    fun loadMasteredCampaigns(userId: String) {
        firestore.collection("campaigns")
            .whereEqualTo("masterID", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = "Error: ${error.message}"
                    return@addSnapshotListener
                }

                val campaigns = snapshot?.documents?.mapNotNull { doc ->
                    Campaign(
                        idCampaign = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        masterID = doc.getString("masterID") ?: "",
                        accessCode = doc.getString("accessCode") ?: "",
                        players = doc.get("players") as? List<DocumentReference> ?: emptyList()
                    )
                } ?: emptyList()

                _masteredCampaigns.value = campaigns
            }
    }

    fun loadPlayedCampaigns(userId: String) {
        val userRef = firestore.collection("users").document(userId)

        playedCampaignsListener = firestore.collection("campaigns")
            .whereArrayContains("players", userRef)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = "Error: ${error.message}"
                    return@addSnapshotListener
                }

                val campaigns = snapshot?.documents?.mapNotNull { doc ->
                    Campaign(
                        idCampaign = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        masterID = doc.getString("masterID") ?: "",
                        accessCode = doc.getString("accessCode") ?: "",
                        players = doc.get("players") as? List<DocumentReference> ?: emptyList()
                    )
                } ?: emptyList()

                val filteredCampaigns = campaigns.filter { it.masterID != userId }

                _playedCampaigns.value = filteredCampaigns
            }
    }

    fun createNewCampaign(
        title: String,
        description: String,
        masterId: String,
        accessCode: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newCampaign = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "masterID" to masterId,
                    "accessCode" to accessCode,
                    "players" to emptyList<DocumentReference>()
                )

                firestore.collection("campaigns")
                    .add(newCampaign)
                    .await()

                onSuccess()
            } catch (e: Exception) {
                onError("Error al crear campaña: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCampaignByAccessCode(
        accessCode: String,
        onSuccess: (Campaign) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("campaigns")
            .whereEqualTo("accessCode", accessCode)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    try {
                        val playersRaw = doc.get("players") as? List<*> ?: emptyList<Any>()
                        val players = playersRaw.mapNotNull {
                            when (it) {
                                is DocumentReference -> it
                                is String -> firestore.document("users/$it")
                                else -> null
                            }
                        }

                        val campaign = Campaign(
                            idCampaign = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            masterID = doc.getString("masterID") ?: "",
                            accessCode = doc.getString("accessCode") ?: "",
                            players = players
                        )

                        onSuccess(campaign)
                    } catch (e: Exception) {
                        onError("Error al deserializar campaña: ${e.message}")
                    }
                } else {
                    onError("Código de acceso inválido")
                }
            }
            .addOnFailureListener {
                onError("Error de conexión: ${it.message}")
            }
    }


    fun joinCampaign(
        accessCode: String,
        userId: String,
        characterName: String,
        onSuccess: (campaignId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val campaignQuery = firestore.collection("campaigns")
                    .whereEqualTo("accessCode", accessCode)
                    .limit(1)
                    .get()
                    .await()

                if (campaignQuery.isEmpty) throw Exception("Código de acceso inválido")

                val campaignDoc = campaignQuery.documents[0]
                val campaignId = campaignDoc.id
                val campaignRef = firestore.collection("campaigns").document(campaignId)
                val userRef = firestore.collection("users").document(userId)

                firestore.runTransaction { transaction ->

                    val campaignSnapshot = transaction.get(campaignRef)
                    val userSnapshot = transaction.get(userRef)

                    val currentPlayers = campaignSnapshot.get("players") as? List<DocumentReference> ?: emptyList()
                    val currentCharacters = userSnapshot.get("charactersOwned") as? List<DocumentReference> ?: emptyList()


                    if (currentPlayers.any { it.id == userId }) {
                        throw Exception("Ya estás en esta campaña")
                    }

                    val characterRef = firestore.collection("characters").document()
                    transaction.update(campaignRef, "players", currentPlayers + userRef)

                    transaction.set(characterRef, hashMapOf(
                        "userId" to userId,
                        "campaignId" to campaignId,
                        "characterName" to characterName
                    ))

                    transaction.update(userRef, "charactersOwned", currentCharacters + characterRef)
                }.await()

                onSuccess(campaignId)
            } catch (e: Exception) {
                onError(e.message ?: "Error uniéndose a la campaña")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getNotes(campaignId: String): Flow<List<Note>> {
        return firestore.collection("campaigns/$campaignId/notes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    Note(
                        idNote = doc.id,
                        title = doc.getString("title") ?: "",
                        content = doc.getString("text") ?: "",
                        authorID = doc.getString("authorId") ?: "",
                        date = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    )
                }
            }
    }

    fun addNote(
        campaignId: String,
        text: String,
        authorId: String,
        authorName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val noteData = hashMapOf(
            "text" to text,
            "authorId" to authorId,
            "authorName" to authorName,
            "timestamp" to Timestamp.now()
        )

        firestore.collection("campaigns/$campaignId/notes")
            .add(noteData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar nota") }
    }

    fun loadCampaignParticipants(campaignId: String) {
        viewModelScope.launch {
            try {
                val campaignDoc = firestore.collection("campaigns").document(campaignId).get().await()
                val masterID = campaignDoc.getString("masterID") ?: ""
                val playerRefs = campaignDoc.get("players") as? List<DocumentReference> ?: emptyList()


                val allUserRefs = (listOf(firestore.document("users/$masterID")) + playerRefs
                    .distinctBy { it.id }
                    .filter { it.id != masterID })


                val usersMap = allUserRefs.map { ref ->
                    async {
                        val userDoc = ref.get().await()
                        ref.id to (userDoc.getString("nickname") ?: "Sin nombre")
                    }
                }.awaitAll().toMap()

                val charactersSnapshot = firestore.collection("characters")
                    .whereEqualTo("campaignId", campaignId)
                    .get()
                    .await()

                val characterMap = charactersSnapshot.documents.associate { doc ->
                    val userId = doc.getString("userId") ?: ""
                    val characterName = doc.getString("characterName") ?: "Sin personaje"
                    val characterId = doc.id
                    userId to Pair(characterName, characterId)
                }
                val participantsList = mutableListOf<Participant>()

                participantsList.add(
                    Participant(
                        userId = masterID,
                        nickname = usersMap[masterID] ?: "Master",
                        characterName = "Master",
                        characterID = "",
                        isMaster = true
                    )
                )

                playerRefs.forEach { playerRef ->
                    val userId = playerRef.id
                    if (userId != masterID) {
                        val nickname = usersMap[userId] ?: "Jugador desconocido"
                        val (characterName, characterId) = characterMap[userId] ?: ("Sin personaje" to "")

                        participantsList.add(
                            Participant(
                                userId = userId,
                                nickname = nickname,
                                characterName = characterName,
                                characterID = characterId,
                                isMaster = false
                            )
                        )
                    }
                }

                _participants.value = participantsList
            } catch (e: Exception) {
                Log.e("CampaignViewModel", "Error loading participants: ${e.message}")
            }
        }
    }

    fun loadCampaignNpcs(campaignId: String) {
        viewModelScope.launch {
            try {
                val result = firestore.collection("npcs")
                    .whereEqualTo("campaignId", campaignId)
                    .get()
                    .await()

                _npcs.value = result.documents.map { doc ->
                    doc.toObject(NonPlayableCharacter::class.java)?.copy(characterId = doc.id) ?: NonPlayableCharacter()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading NPCs: ${e.message}"
            }
        }
    }




    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        campaignsListener?.remove()
        playedCampaignsListener?.remove()
        super.onCleared()
    }
}

class CampaignViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampaignViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}