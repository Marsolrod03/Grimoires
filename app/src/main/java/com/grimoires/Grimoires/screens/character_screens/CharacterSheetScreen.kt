package com.grimoires.Grimoires.screens.character_screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import androidx.compose.material3.Divider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import com.grimoires.Grimoires.domain.model.Attributes
import com.grimoires.Grimoires.ui.models.CatalogViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    character: PlayableCharacter,
    catalogViewModel: CatalogViewModel,
    onEditClick: () -> Unit
) {

    val classList by catalogViewModel.classes.collectAsState()
    val characterClass = classList.find { it.name == character.characterClass }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.name, fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Character", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B3A2E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LabeledText("NAME", character.name)
            LabeledText("RACE", character.race)
            LabeledText("CLASS", character.characterClass)
            LabeledText("LEVEL", character.level.toString())
            LabeledText("ALIGNMENT", character.alignment)

            Spacer(modifier = Modifier.height(16.dp))
            Text("ATTRIBUTES:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF8B3A2E))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = characterClass?.description ?: "No description available.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("STATS:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF8B3A2E))

            StatsGrid(attributes = character.attributes)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { /* TODO: Navigate to Equipment */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3A2E))
                ) {
                    Text("EQUIPMENT", color = Color.White)
                }

                Button(
                    onClick = { /* TODO: Navigate to Spells */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3A2E))
                ) {
                    Text("SPELLS", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LabeledText(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label:", color = Color(0xFF8B3A2E), fontWeight = FontWeight.Bold)
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Black)
        Text(value, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun StatsGrid(attributes: Attributes) {
    val statLabels = listOf("STR", "DEX", "CON", "INT", "WIS", "CHA")
    val statValues = listOf(
        attributes.strength,
        attributes.dexterity,
        attributes.constitution,
        attributes.intelligence,
        attributes.wisdom,
        attributes.charisma
    )

    Column {
        for (i in statLabels.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("${statLabels[i]}  ${statValues[i]}")
                Text("${statLabels[i + 1]}  ${statValues[i + 1]}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
