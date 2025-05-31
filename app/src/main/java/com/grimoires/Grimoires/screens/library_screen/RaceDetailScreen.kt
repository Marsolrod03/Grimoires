package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grimoires.Grimoires.domain.model.Race

@Composable
fun RaceDetailScreen(race: Race, onBack: () -> Unit) {
    DetailScaffold(title = race.name, onBack = onBack) {
        Column {
            Text("Description:", fontWeight = FontWeight.Bold)
            Text(race.description)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Size:", fontWeight = FontWeight.Bold)
            Text(race.size)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Speed: ${race.speed} ft")

            Spacer(modifier = Modifier.height(12.dp))
            Text("Darkvision: ${if (race.darkvision) "Sí" else "No"}")

            Spacer(modifier = Modifier.height(12.dp))
            Text("Languages:", fontWeight = FontWeight.Bold)
            race.languages.forEach {
                Text("- $it")
            }
        }
    }
}
