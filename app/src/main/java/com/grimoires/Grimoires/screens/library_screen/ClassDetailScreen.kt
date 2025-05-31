package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.grimoires.Grimoires.domain.model.CharacterClass

@Composable
fun ClassDetailScreen(characterClass: CharacterClass, onBack: () -> Unit) {
    DetailScaffold(title = characterClass.name, onBack = onBack) {
        Column {
            Text("Description:", fontWeight = FontWeight.Bold)
            Text(characterClass.description)
        }
    }
}
