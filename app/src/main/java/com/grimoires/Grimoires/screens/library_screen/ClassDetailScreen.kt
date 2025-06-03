package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoires.Grimoires.domain.model.CharacterClass

@Composable
fun ClassDetailScreen(characterClass: CharacterClass, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    DetailScaffold(title = characterClass.name, onBack = onBack) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Description:", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(characterClass.description)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Abilities:", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            characterClass.abilities.forEach { ability ->
                Text("• $ability")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}