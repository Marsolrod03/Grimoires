package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.DocumentId

data class Usuario(
    @DocumentId val idUsuario: String = "",
    val name: String = "",
    var nickname: String = "",
    val email: String = "",
    val playedCampaigns: List<String> = emptyList(),
    val charactersOwned: List<String> = emptyList()
)