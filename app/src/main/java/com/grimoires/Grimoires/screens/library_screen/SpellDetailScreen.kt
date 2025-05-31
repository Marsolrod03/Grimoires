package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grimoires.Grimoires.domain.model.Spell

@Composable
fun SpellDetailScreen(spell: Spell, onBack: () -> Unit) {
    DetailScaffold(title = spell.name, onBack = onBack) {
        Column {
            Text("Level: ${spell.level}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Description:", fontWeight = FontWeight.Bold)
            Text(spell.description)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Classes:", fontWeight = FontWeight.Bold)
            spell.charClass.forEach {
                Text("- $it")
            }
        }
    }
}
