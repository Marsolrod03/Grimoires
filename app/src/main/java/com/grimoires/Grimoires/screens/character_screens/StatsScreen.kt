

package com.grimoires.Grimoires.screens.character_screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.domain.model.Attributes
import com.grimoires.Grimoires.ui.models.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavHostController,
    statsViewModel: StatsViewModel,
    characterId: String
) {
    var isInitialized by remember { mutableStateOf(false) }

    val attributes = statsViewModel.attributes

    var strength by remember { mutableStateOf("10") }
    var dexterity by remember { mutableStateOf("10") }
    var constitution by remember { mutableStateOf("10") }
    var intelligence by remember { mutableStateOf("10") }
    var wisdom by remember { mutableStateOf("10") }
    var charisma by remember { mutableStateOf("10") }

    LaunchedEffect(characterId) {
        statsViewModel.loadAttributes(characterId)
    }

    LaunchedEffect(attributes) {
        if (!isInitialized) {
            strength = attributes.strength.toString()
            dexterity = attributes.dexterity.toString()
            constitution = attributes.constitution.toString()
            intelligence = attributes.intelligence.toString()
            wisdom = attributes.wisdom.toString()
            charisma = attributes.charisma.toString()
            isInitialized = true
        }
    }

    fun calculateBonus(value: String): Int {
        val intValue = value.toIntOrNull() ?: 10
        return (intValue - 10) / 2
    }

    fun formatBonus(bonus: Int): String {
        return if (bonus >= 0) "+$bonus" else bonus.toString()
    }

    Scaffold(
        containerColor = Color(0xFFE5D3B3),
        topBar = {
            TopAppBar(
                title = { Text("STATS") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val newAttributes = Attributes(
                        strength = strength.toIntOrNull() ?: 10,
                        dexterity = dexterity.toIntOrNull() ?: 10,
                        constitution = constitution.toIntOrNull() ?: 10,
                        intelligence = intelligence.toIntOrNull() ?: 10,
                        wisdom = wisdom.toIntOrNull() ?: 10,
                        charisma = charisma.toIntOrNull() ?: 10
                    )
                    statsViewModel.updateAttributes(characterId, newAttributes)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C9E87)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("SAVE STATS", fontSize = 18.sp, color = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AttributeRow("STRENGTH", strength, { strength = it }, formatBonus(calculateBonus(strength)))
            AttributeRow("DEXTERITY", dexterity, { dexterity = it }, formatBonus(calculateBonus(dexterity)))
            AttributeRow("CONSTITUTION", constitution, { constitution = it }, formatBonus(calculateBonus(constitution)))
            AttributeRow("INTELLIGENCE", intelligence, { intelligence = it }, formatBonus(calculateBonus(intelligence)))
            AttributeRow("WISDOM", wisdom, { wisdom = it }, formatBonus(calculateBonus(wisdom)))
            AttributeRow("CHARISMA", charisma, { charisma = it }, formatBonus(calculateBonus(charisma)))
        }
    }
}


@Composable
fun AttributeRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    bonus: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.width(150.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.width(70.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                )

                Text(
                    text = bonus,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .width(50.dp)
                        .padding(start = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}