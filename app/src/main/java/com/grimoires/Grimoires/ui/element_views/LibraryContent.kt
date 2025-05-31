package com.grimoires.Grimoires.ui.element_views

import com.grimoires.Grimoires.domain.model.CharacterClass
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.LibraryItem
import com.grimoires.Grimoires.domain.model.Race
import com.grimoires.Grimoires.domain.model.Spell


fun Race.toLibraryItem() = LibraryItem(raceId, name, description, "race")
fun CharacterClass.toLibraryItem() = LibraryItem(classId, name, description, "class")
fun Item.toLibraryItem() = LibraryItem(itemId, name, description, "item")
fun Spell.toLibraryItem() = LibraryItem(spellId, name, description, "spell")
