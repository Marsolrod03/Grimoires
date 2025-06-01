package com.grimoires.Grimoires.domain.model

data class Campaign(
    val idCampaign: String = "",
    var accessCode: String = "",
    val title: String = "",
    val genre: String = "",
    val description: String = "",
    val masterID: String = "",
    val characters: List<String> = emptyList(),
    val npcs: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)