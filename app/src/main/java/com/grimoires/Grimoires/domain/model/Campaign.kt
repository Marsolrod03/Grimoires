package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.DocumentReference

data class Campaign(
    val idCampaign: String = "",
    var accessCode: String = "",
    val title: String = "",
    val genre: String = "",
    val description: String = "",
    val masterID: String = "",
    val players: List<DocumentReference> = emptyList(),
    val npcs: List<DocumentReference> = emptyList(),
    val notes: List<DocumentReference> = emptyList()
)