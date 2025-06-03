package com.grimoires.Grimoires.screens.character_screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.domain.model.Spell
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.models.StatsViewModel
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.leafGreen
import com.grimoires.Grimoires.ui.theme.parchment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCharacterScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    statsViewModel: StatsViewModel,
    onSave: (PlayableCharacter, List<Spell>, List<Item>) -> Unit
) {
    val catalogViewModel: CatalogViewModel = viewModel()
    val characterViewModel: PlayableCharacterViewModel = viewModel()

    val raceList by catalogViewModel.races.collectAsState()
    val classList by catalogViewModel.classes.collectAsState()

    val raceOptions = raceList.map { it.race }
    val classOptions = classList.map { it.name }
    val levelOptions = (1..20).map { it.toString() }
    val alignmentOptions = listOf(
        "Lawful Good", "Chaotic Good", "Neutral Good",
        "Lawful Neutral", "Neutral", "Chaotic Neutral",
        "Lawful Evil", "Neutral Evil", "Chaotic Evil"
    )

    var name by rememberSaveable { mutableStateOf("") }
    var race by rememberSaveable { mutableStateOf("") }
    var charClass by rememberSaveable { mutableStateOf("") }
    var level by rememberSaveable { mutableStateOf("") }
    var alignment by rememberSaveable { mutableStateOf("") }
    var selectedDescription by rememberSaveable { mutableStateOf("") }
    val selectedSpells = remember { mutableStateListOf<Spell>() }
    val selectedItems = remember { mutableStateListOf<Item>() }

    val characterId = remember { FirebaseFirestore.getInstance().collection("characters").document().id }

    val isInputValid = validateCharacterInputs(name, race, charClass, level, alignment)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Character",
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        },
        containerColor = parchment
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CustomInputField("NAME:", name) { name = it }
            Spacer(modifier = Modifier.height(16.dp))
            DropdownSelector("RACE:", raceOptions, race) { race = it }
            Spacer(modifier = Modifier.height(16.dp))
            DropdownSelector("CLASS:", classOptions, charClass) {
                charClass = it
                selectedDescription = classList.find { cls -> cls.name == it }?.description ?: ""
            }

            if (selectedDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            DropdownSelector("LEVEL:", levelOptions, level) { level = it }
            Spacer(modifier = Modifier.height(16.dp))
            DropdownSelector("ALIGNMENT:", alignmentOptions, alignment) { alignment = it }
            Spacer(modifier = Modifier.height(24.dp))

            SectionWithButton("CHOOSE SPELLS:", "SPELLS") {
                if (isInputValid) {
                    navController.navigate("spells_screen/$charClass/$characterId")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionWithButton("EQUIPMENT:", "EQUIPMENT") {
                if (isInputValid) {
                    navController.navigate("equipment_screen/$characterId")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionWithButton("STATS:", "STATS") {
                if (isInputValid) {
                    navController.navigate("stats_screen/$characterId")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (!isInputValid) return@Button

                    val uid = userViewModel.uid ?: return@Button
                    val levelInt = level.toIntOrNull() ?: 1

                    val character = PlayableCharacter(
                        characterId = characterId,
                        characterName = name,
                        characterClass = charClass,
                        race = race,
                        alignment = alignment,
                        attributes = statsViewModel.attributes,
                        level = levelInt,
                        physicalDescription = selectedDescription,
                        campaignId = "",
                        inventory = emptyList(),
                        spells = emptyList(),
                        userId = uid
                    )

                    characterViewModel.addCharacterToFirestore(
                        character,
                        onSuccess = {
                            val spellIds = selectedSpells.mapNotNull { it.spellId }
                            val itemIds = selectedItems.mapNotNull { it.itemId }

                            characterViewModel.saveSelectedSpells(character.characterId, spellIds)
                            characterViewModel.saveSelectedItems(character.characterId, itemIds)

                            onSave(character, selectedSpells, selectedItems)
                            navController.popBackStack()
                        },
                        onError = {
                            Log.e("AddCharacter", "Error saving character")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = leafGreen,
                    contentColor = Color.White
                ),
                enabled = isInputValid
            ) {
                Text("SAVE CHARACTER", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            }
        }
    }
}


private fun validateCharacterInputs(
    name: String,
    race: String,
    charClass: String,
    level: String,
    alignment: String
): Boolean {
    return name.isNotBlank() &&
            race.isNotBlank() &&
            charClass.isNotBlank() &&
            level.isNotBlank() &&
            alignment.isNotBlank()
}

@Composable
fun CustomInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = deepBrown
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = deepBrown,
                unfocusedBorderColor = deepBrown,
                cursorColor = deepBrown
            )
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = deepBrown
        )
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedOption,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = deepBrown,
                    unfocusedBorderColor = deepBrown,
                    cursorColor = deepBrown
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionWithButton(title: String, buttonText: String, onButtonClick: () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = deepBrown
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = deepBrown,
                contentColor = Color.White
            )
        ) {
            Text(buttonText.uppercase(), fontFamily = FontFamily.Serif)
        }
    }
}


