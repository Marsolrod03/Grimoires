package com.grimoires.Grimoires.screens.catalog_screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.domain.model.Spell
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.leafGreen
import com.grimoires.Grimoires.ui.theme.parchment
import com.grimoires.Grimoires.ui.theme.textDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellsSelectionScreen(
    navController: NavHostController,
    characterClass: String,
    characterId: String,
    viewModel: CatalogViewModel = viewModel(LocalContext.current as ViewModelStoreOwner),
    characterViewModel: PlayableCharacterViewModel = viewModel()
) {
    val allSpells by viewModel.spells.collectAsState()
    val selectedSpells = remember { mutableStateListOf<Spell>() }
    var selectedLevel by remember { mutableStateOf("All") }

    val spellLevels = remember(allSpells) {
        listOf("All") + allSpells.map { it.level.toString() }.distinct().sorted()
    }

    val filteredSpells = remember(allSpells, selectedLevel, characterClass) {
        allSpells.filter { spell ->
            val matchesLevel = selectedLevel == "All" || spell.level.toString() == selectedLevel
            val matchesClass = spell.charClass.any { it.equals(characterClass, ignoreCase = true) }
            matchesLevel && matchesClass
        }
    }


    LaunchedEffect(characterId) {
        characterViewModel.loadSpellsByIds(characterId) { equippedSpells ->
            selectedSpells.clear()
            selectedSpells.addAll(equippedSpells)
        }

        if (allSpells.isEmpty()) {
            viewModel.fetchSpells()
        }
    }

    Scaffold(
        containerColor = parchment,
        topBar = {
            TopAppBar(
                title = { Text("Spells", fontSize = 28.sp, fontFamily = FontFamily.Serif, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val spellIds = selectedSpells.map { it.spellId }
                    characterViewModel.saveSelectedSpells(characterId, spellIds)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedSpells.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = leafGreen)
            ) {
                Text("SAVE SPELLS", color = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("SPELL LEVEL:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedLevel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor().width(150.dp),
                        label = { Text("Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        spellLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    selectedLevel = level
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredSpells.isEmpty()) {
                Text("No spells available for $characterClass.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        items = filteredSpells,
                        key = { it.spellId }
                    ) { spell ->
                        SpellItemCard(
                            spell = spell,
                            isSelected = selectedSpells.contains(spell),
                            onSelectionChange = { selected ->
                                if (selected) {
                                    if (!selectedSpells.contains(spell)) {
                                        selectedSpells.add(spell)
                                    }
                                } else {
                                    selectedSpells.remove(spell)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpellItemCard(
    spell: Spell,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = parchment),
        border = BorderStroke(2.dp, deepBrown),
        onClick = { onSelectionChange(!isSelected) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RetroCheckbox(checked = isSelected, onCheckedChange = onSelectionChange)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(spell.name, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Level ${spell.level}", color = deepBrown, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(spell.description, maxLines = 2, fontFamily = FontFamily.Serif)
            }
        }
    }
}

@Composable
fun RetroCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .border(2.dp, deepBrown, RoundedCornerShape(4.dp))
            .background(if (checked) deepBrown else Color.Transparent)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Box(
                Modifier
                    .size(12.dp)
                    .background(Color.White, RoundedCornerShape(2.dp))
            )
        }
    }
}
