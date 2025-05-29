package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Nota(
    val idNota: String = "",
    val titulo: String = "",
    var contenido: String = "",
    val usuarioID: String = "",
    val campañaID: String = "",
    @ServerTimestamp val fecha: Date? = null
)

data class Notificacion(
    val idNotificacion: String = "",
    val usuarioID: String = "",
    val mensaje: String = "",
    @ServerTimestamp val fecha: Date? = null,
    var leida: Boolean = false
)