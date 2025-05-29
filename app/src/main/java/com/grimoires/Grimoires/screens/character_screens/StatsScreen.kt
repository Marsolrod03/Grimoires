

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
    LaunchedEffect(characterId) {
        statsViewModel.loadAttributes(characterId)
    }

    var strength by remember { mutableStateOf("10") }
    var dexterity by remember { mutableStateOf("10") }
    var constitution by remember { mutableStateOf("10") }
    var intelligence by remember { mutableStateOf("10") }
    var wisdom by remember { mutableStateOf("10") }
    var charisma by remember { mutableStateOf("10") }

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
                    val attributes = Attributes(
                        strength = strength.toIntOrNull() ?: 10,
                        dexterity = dexterity.toIntOrNull() ?: 10,
                        constitution = constitution.toIntOrNull() ?: 10,
                        intelligence = intelligence.toIntOrNull() ?: 10,
                        wisdom = wisdom.toIntOrNull() ?: 10,
                        charisma = charisma.toIntOrNull() ?: 10
                    )
                    statsViewModel.updateAttributes(attributes)
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

            AttributeRow(
                label = "STRENGTH",
                value = strength,
                onValueChange = {
                    val num = it.toIntOrNull()
                    if (it.isEmpty() || (num != null && num in 1..20)) strength = it
                },
                bonus = formatBonus(calculateBonus(strength))
            )

            AttributeRow(
                label = "DEXTERITY",
                value = dexterity,
                onValueChange = {
                    val num = it.toIntOrNull()
                    if (it.isEmpty() || (num != null && num in 1..20)) dexterity = it
                },
                bonus = formatBonus(calculateBonus(dexterity))
            )

            AttributeRow(
                label = "CONSTITUTION",
                value = constitution,
                onValueChange = {
                    val num = it.toIntOrNull()
                    if (it.isEmpty() || (num != null && num in 1..20)) constitution = it
                },
                bonus = formatBonus(calculateBonus(constitution))
            )

            AttributeRow(
                label = "INTELLIGENCE",
                value = intelligence,
                onValueChange = {
                    val num = it.toIntOrNull()
                    if (it.isEmpty() || (num != null && num in 1..20)) intelligence = it
                },
                bonus = formatBonus(calculateBonus(intelligence))
            )

            AttributeRow(
                label = "WISDOM",
                value = wisdom,
                onValueChange = {
                    val num = it.toIntOrNull()
                    if (it.isEmpty() || (num != null && num in 1..20)) wisdom = it
                },
                bonus = formatBonus(calculateBonus(wisdom))
            )

            AttributeRow(
                label = "CHARISMA",
                value = charisma,
                onValueChange = {
                    val num = it.toIntOrNull()
                    if (it.isEmpty() || (num != null && num in 1..20)) charisma = it
                },
                bonus = formatBonus(calculateBonus(charisma))
            )

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