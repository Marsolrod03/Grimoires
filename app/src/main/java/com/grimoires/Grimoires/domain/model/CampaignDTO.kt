package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.DocumentReference

data class Campaign(
    var idCampaign: String = "",
    var accessCode: String = "",
    var title: String = "",
    var description: String = "",
    var masterID: String = "",
    var players: List<DocumentReference> = emptyList(),
)

data class Participant(
    val userId: String,
    val nickname: String,
    val characterName: String,
    val characterID: String,
    val isMaster: Boolean,
)