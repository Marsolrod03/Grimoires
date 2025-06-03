package com.grimoires.Grimoires.ui.element_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontFamily
import com.grimoires.Grimoires.domain.model.CharacterClass
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.LibraryItem
import com.grimoires.Grimoires.domain.model.Race
import com.grimoires.Grimoires.domain.model.Spell
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan
import com.grimoires.Grimoires.ui.theme.oak
import com.grimoires.Grimoires.ui.theme.parchment



fun Race.toLibraryItem() = LibraryItem(id = raceId, title = race, description = description, type = "race")
fun CharacterClass.toLibraryItem() = LibraryItem(id = classId, title = name, description = description, type = "class")
fun Item.toLibraryItem() = LibraryItem(itemId, name, description, "item")
fun Spell.toLibraryItem() = LibraryItem(spellId, name, description, "spell")

@Composable
fun LibraryItemCard(
    item: LibraryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, deepBrown),
        colors = CardDefaults.cardColors(containerColor = parchment)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title.uppercase(),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                color = oak
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Black
            )
        }
    }
}
