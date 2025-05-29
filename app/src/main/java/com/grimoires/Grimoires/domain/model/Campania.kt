package com.grimoires.Grimoires.domain.model

data class Campaña(
    val idCampaña: String = "",
    var codigoAcceso: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val masterID: String = "",
    val personajes: List<String> = emptyList(),
    val personajesNoJugables: List<String> = emptyList(),
    val notas: List<String> = emptyList()
)