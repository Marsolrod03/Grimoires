package com.grimoires.Grimoires.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.grimoires.Grimoires.data.network.NotificationService
import com.grimoires.Grimoires.domain.model.Campaign
import com.grimoires.Grimoires.domain.model.Note
import com.grimoires.Grimoires.domain.model.User
import kotlinx.coroutines.flow.*

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

    private val _participants = MutableStateFlow<List<User>>(emptyList())
    val participants = _participants.asStateFlow()

    private var campaignsListener: ListenerRegistration? = null

    fun getCampaign(campaignId: String): Flow<Campaign?> {
        return firestore.collection("campaigns").document(campaignId)
            .snapshots()
            .map { it.toObject(Campaign::class.java) }
    }

    fun loadMasteredCampaigns(currentUserId: String) {
        firestore.collection("campaigns")
            .whereEqualTo("masterID", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = "Error: ${error.message}"
                    return@addSnapshotListener
                }
                val campaigns = snapshot?.documents?.mapNotNull { doc ->
                    val title = doc.getString("title") ?: ""
                    val description = doc.getString("description") ?: ""
                    val masterID = doc.getString("masterID") ?: ""
                    val accessCode = doc.getString("accessCode") ?: ""
                    val characters = doc.get("characters") as? List<String> ?: emptyList()

                    Campaign(
                        idCampaign = doc.id,
                        title = title,
                        description = description,
                        masterID = masterID,
                        accessCode = accessCode,
                        characters = characters
                    )
                } ?: emptyList()
                _masteredCampaigns.value = campaigns
            }
    }

    fun loadPlayedCampaigns(currentUserCharacterId: String) {
        firestore.collection("campaigns")
            .whereArrayContains("characters", currentUserCharacterId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = "Error: ${error.message}"
                    return@addSnapshotListener
                }
                val campaigns = snapshot?.documents?.mapNotNull {
                    it.toObject(Campaign::class.java)?.copy(idCampaign = it.id)
                } ?: emptyList()
                _playedCampaigns.value = campaigns
            }
    }

    fun joinCampaign(
        campaignId: String,
        characterId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val campaignRef = firestore.collection("campaigns").document(campaignId)

        firestore.runTransaction { transaction ->
            val campaignSnapshot = transaction.get(campaignRef)
            val campaign = campaignSnapshot.toObject(Campaign::class.java)
                ?: throw Exception("Campaign not found")
            if (campaign.characters.contains(characterId)) {
                throw Exception("Este personaje ya está en la campaña")
            }
            val updatedCharacters = campaign.characters.toMutableList()
            updatedCharacters.add(characterId)
            transaction.update(campaignRef, "characters", updatedCharacters)
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onError(e.message ?: "Error uniendo personaje a campaña")
        }
    }

    fun createNewCampaign(
        title: String,
        description: String,
        masterId: String,
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val campaign = Campaign(
            idCampaign = "",
            title = title,
            description = description,
            masterID = masterId,
            accessCode = code,
            characters = listOf(masterId)
        )

        firestore.collection("campaigns")
            .add(campaign)
            .addOnSuccessListener { document ->
                firestore.collection("campaigns").document(document.id)
                    .update("idCampaign", document.id)
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Error al guardar campaña")
            }
    }

    fun getCampaignByCode(
        code: String,
        onSuccess: (Campaign) -> Unit,
        onError: () -> Unit
    ) {
        firestore.collection("campaigns")
            .whereEqualTo("accessCode", code)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val campaign = doc.toObject(Campaign::class.java)?.copy(idCampaign = doc.id)
                    if (campaign != null) {
                        onSuccess(campaign)
                    } else {
                        onError()
                    }
                } else {
                    onError()
                }
            }
            .addOnFailureListener {
                onError()
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
                        userID = doc.getString("authorId") ?: "",
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
        firestore.collection("campaigns").document(campaignId).get()
            .addOnSuccessListener { doc ->
                val characterIds = doc.get("characters") as? List<String> ?: emptyList()
                if (characterIds.isNotEmpty()) {
                    firestore.collection("characters")
                        .whereIn("characterId", characterIds)
                        .get()
                        .addOnSuccessListener { charsSnapshot ->
                            val userIds = charsSnapshot.documents.mapNotNull {
                                it.getString("userId")
                            }.distinct()

                            if (userIds.isNotEmpty()) {
                                firestore.collection("users")
                                    .whereIn("idUser", userIds)
                                    .get()
                                    .addOnSuccessListener { usersSnapshot ->
                                        val users = usersSnapshot.documents.mapNotNull { doc ->
                                            doc.toObject(User::class.java)
                                        }
                                        _participants.value = users
                                    }
                            }
                        }
                }
            }
    }

    fun sendNotificationToCampaign(
        title: String,
        message: String,
        senderId: String
    ) {
        val tokens = _participants.value
            .filter { it.idUser!= senderId && it.fcmToken.isNotBlank() }
            .map { it.fcmToken }

        if (tokens.isNotEmpty()) {
            NotificationService.sendMulticastNotification(tokens, title, message)
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        campaignsListener?.remove()
        super.onCleared()
    }
}
