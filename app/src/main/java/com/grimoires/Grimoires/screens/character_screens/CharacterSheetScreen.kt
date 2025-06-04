package com.grimoires.Grimoires.screens.character_screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.navigation.NavController

import com.grimoires.Grimoires.domain.model.Attributes
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.StatsViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.oak
import com.grimoires.Grimoires.ui.theme.parchment
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.Spell


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    character: PlayableCharacter,
    catalogViewModel: CatalogViewModel,
    statsViewModel: StatsViewModel,
    onEditClick: () -> Unit,
    navController: NavController
) {

    val attributes = statsViewModel.attributes
    val items by remember { mutableStateOf<List<Item>>(emptyList()) }
    val spells by remember { mutableStateOf<List<Spell>>(emptyList()) }

    LaunchedEffect(character.characterId) {
        statsViewModel.loadAttributes(character.characterId)
        statsViewModel.loadItemsForCharacter(character.characterId)
        statsViewModel.loadSpellsForCharacter(character.characterId)

    }
    val classList by catalogViewModel.classes.collectAsState()
    val characterClass = classList.find { it.name.trim().equals(character.characterClass.trim(), ignoreCase = true) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.characterName, fontSize = 24.sp, fontFamily = Serif) },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {navController.navigate("edit_character/${character.characterId}")}) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Character", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = deepBrown,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(parchment)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)

        ) {
            LabeledText("NAME", character.characterName)
            LabeledText("RACE", character.race)
            LabeledText("CLASS", character.characterClass)
            LabeledText("LEVEL", character.level.toString())
            LabeledText("ALIGNMENT", character.alignment)

            Spacer(modifier = Modifier.height(16.dp))
            Text("ATTRIBUTES:", style = MaterialTheme.typography.titleMedium, color = deepBrown, fontFamily = Serif, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = oak)
            Spacer(modifier = Modifier.height(6.dp))
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
            Text("STATS:", style = MaterialTheme.typography.titleMedium, color = deepBrown,fontWeight = FontWeight.Bold, fontFamily = Serif)
            Spacer(modifier = Modifier.height(6.dp))
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = oak)
            Spacer(modifier = Modifier.height(6.dp))
            StatsGrid(attributes = attributes)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("EQUIPMENT", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Serif, color = deepBrown)
                TextButton(onClick = {
                    navController.navigate("equipment_select/${character!!.characterId}")
                }) {
                    Text("My Equipment", color = deepBrown, fontFamily = Serif)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = oak)
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (items.isEmpty()) {

            } else {
                items.take(3).forEach { item ->
                    Text("• ${item.name}", fontFamily = Serif)
                }
                if (items.size > 3) {
                    Text("...and ${items.size - 3} more", fontFamily = Serif)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SPELLS", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Serif,color = deepBrown)
                TextButton(onClick = {
                    navController.navigate("spells_select/${character!!.characterId}/${character!!.characterClass}")
                }) {
                    Text("My Spells", color = deepBrown, fontFamily = Serif)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = oak)
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (spells.isEmpty()) {

            } else {
                spells.take(3).forEach { spell ->
                    Text("• ${spell.name} (Lv ${spell.level})", fontFamily = Serif)
                }
                if (spells.size > 3) {
                    Text("...and ${spells.size - 3} more", fontFamily = Serif)
                }
            }
        }
    }
}

@Composable
fun LabeledText(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label:", color = deepBrown, fontWeight = FontWeight.Bold, fontFamily = Serif,fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = oak)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, modifier = Modifier.padding(start = 8.dp), color = Color.Black, fontFamily = Serif)
    }
}
@Composable
fun StatsGrid(attributes: Attributes) {
    fun calculateBonus(score: Int): Int = (score - 10) / 2

    val stats = listOf(
        "STR" to calculateBonus(attributes.strength),
        "DEX" to calculateBonus(attributes.dexterity),
        "CON" to calculateBonus(attributes.constitution),
        "INT" to calculateBonus(attributes.intelligence),
        "WIS" to calculateBonus(attributes.wisdom),
        "CHA" to calculateBonus(attributes.charisma)
    )

    stats.chunked(2).forEach { rowStats ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            rowStats.forEach { (label, bonus) ->
                val formattedBonus = if (bonus >= 0) "+$bonus" else "$bonus"
                Text("$label: $formattedBonus", fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
