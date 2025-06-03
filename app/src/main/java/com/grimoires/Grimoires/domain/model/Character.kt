package com.grimoires.Grimoires.domain.model

import com.google.firebase.firestore.DocumentReference

abstract class Character(
    open val characterId: String = "",
    open val name: String = "",
    open val characterClass: String = "",
    open val race: String = "",
    open val alignment: String = "",
    open val attributes: Attributes = Attributes(),
    open var level: Int = 1,
    open val physicalDescription: String = "",
    open var campaignId: String = ""
)

data class PlayableCharacter(
    override val characterId: String = "",
    override val name: String = "",
    override val characterClass: String = "",
    override val race: String = "",
    override val alignment: String = "",
    override val attributes: Attributes = Attributes(),
    override var level: Int = 1,
    override val physicalDescription: String = "",
    override var campaignId: String = "",
    val inventory: List<DocumentReference?> = emptyList(),
    val spells: List<DocumentReference?> = emptyList(),
    val userId: String = ""
) : Character(characterId, name, characterClass, race, alignment, attributes, level, physicalDescription, campaignId)

data class NonPlayableCharacter(
    override val characterId: String = "",
    override val name: String = "",
    override val characterClass: String = "",
    override val race: String = "",
    override val alignment: String = "",
    override var level: Int = 1,
    val masterId: String = ""
) : Character(characterId, name, characterClass, race, alignment, level = level)
