package com.grimoires.Grimoires.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Note(
    val idNote: String = "",
    val title: String = "",
    var content: String = "",
    val userID: String = "",
    val authorName: String = "",
    val campaignID: String = "",
    @ServerTimestamp val date: Timestamp? = null
)
