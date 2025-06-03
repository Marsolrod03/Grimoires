package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.PropertyName


data class Attributes(
    val strength: Int = 0,
    val dexterity: Int = 0,
    val constitution: Int = 0,
    val intelligence: Int = 0,
    val wisdom: Int = 0,
    val charisma: Int = 0
)

data class CharacterClass(
    val classId: String = "",
    val name: String = "",
    val description: String = "",
    var abilities: List<String> = emptyList()
)

data class Race(
    val raceId: String = "",
    val description: String = "",
    var race: String = "",
    val size: String = "",
    val speed: Int = 0,
    val languages: List<String> = emptyList(),
    var dark_vision: Boolean = false
)

data class Item(
    val itemId: String = "",
    val name: String = "",
    val type: String = "",
    val description: String = ""
)

data class Spell(
    val spellId: String = "",
    val name: String = "",
    val description: String = "",
    val level: Int = 1,
    @get:PropertyName("class") @set:PropertyName("class")
    var charClass: List<String> = emptyList()
)

data class LibraryItem(
    val id: String,
    val title: String,
    val description: String,
    val type: String

)
