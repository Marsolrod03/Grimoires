package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoires.Grimoires.domain.model.Race

@Composable
fun RaceDetailScreen(race: Race, onBack: () -> Unit) {
    DetailScaffold(title = race.race, onBack = onBack) {
        Column {
            Text("Description:", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,fontSize = 18.sp)
            Text(race.description)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Size:", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,fontSize = 18.sp)
            Text(race.size)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Speed: ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,fontSize = 18.sp)
            Text("${race.speed} ft")

            Spacer(modifier = Modifier.height(12.dp))
            Text("Darkvision: ",fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,fontSize = 18.sp)
            Text("${if (race.dark_vision) "YES" else "NO"}")

            Spacer(modifier = Modifier.height(12.dp))
            Text("Languages:", fontWeight = FontWeight.Bold,fontFamily = FontFamily.Serif,fontSize = 18.sp)
            race.languages.forEach {
                Text("- $it")
            }
        }
    }
}
