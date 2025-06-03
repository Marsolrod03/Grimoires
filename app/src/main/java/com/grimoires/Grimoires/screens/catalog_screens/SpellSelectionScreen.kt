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

    val filteredSpells = remember(allSpells, characterClass, selectedLevel) {
        allSpells.filter { spell ->
            val levelFilter = selectedLevel == "All" || spell.level.toString() == selectedLevel
            val classFilter = characterClass.isEmpty() || spell.charClass.any { it.equals(characterClass, ignoreCase = true) }
            levelFilter && classFilter
        }
    }

    LaunchedEffect(Unit) {
        if (allSpells.isEmpty()) {
            viewModel.fetchSpells()
        }
    }

    Scaffold(
        containerColor = parchment,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Spells",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val spellsIds = selectedSpells.map { it.spellId }
                    characterViewModel.saveSelectedSpells(characterId, spellsIds)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = leafGreen,
                    contentColor = Color.White
                ),
                enabled = selectedSpells.isNotEmpty()
            ) {
                Text("SAVE SPELLS", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SPELL LEVEL:",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    color = textDark,
                    modifier = Modifier.padding(end = 8.dp)
                )
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedLevel,
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().width(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = deepBrown,
                            focusedBorderColor = deepBrown,
                            cursorColor = deepBrown,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark,
                            focusedContainerColor = parchment,
                            unfocusedContainerColor = parchment
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
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
                Text(
                    "No spells available for $characterClass.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSpells) { spell ->
                        SpellItem(
                            spell = spell,
                            isSelected = selectedSpells.contains(spell),
                            onSelectionChange = { selected ->
                                if (selected) selectedSpells.add(spell)
                                else selectedSpells.remove(spell)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpellItem(
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
