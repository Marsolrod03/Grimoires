package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class User(
    @DocumentId val idUser: String = "",
    val name: String = "",
    var nickname: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val playedCampaigns: List<DocumentReference> = emptyList(),
    val charactersOwned: List<DocumentReference> = emptyList()
)