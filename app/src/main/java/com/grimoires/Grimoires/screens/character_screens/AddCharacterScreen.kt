package com.grimoires.Grimoires.screens.character_screens

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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.StatsViewModel
import com.grimoires.Grimoires.ui.models.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCharacterScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    statsViewModel: StatsViewModel,
    onSave: (PlayableCharacter) -> Unit
) {
    val catalogViewModel: CatalogViewModel = viewModel()
    val nickname = userViewModel.nickname
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val raceList by catalogViewModel.races.collectAsState()
    val classList by catalogViewModel.classes.collectAsState()

    val raceOptions = raceList.map { it.name }
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

    val characterId = remember { FirebaseFirestore.getInstance().collection("characters").document().id }

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
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF8A3A2D))
            )
        },
        containerColor = Color(0xFFF6ECDC)
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
                val firestore = FirebaseFirestore.getInstance()
                val levelInt = level.toIntOrNull() ?: 1
                val characterData = mapOf(
                    "id" to characterId,
                    "name" to name,
                    "charClass" to charClass,
                    "race" to race,
                    "level" to levelInt,
                    "alignment" to alignment,
                    "spells" to emptyList<String>(),
                    "userId" to userViewModel.uid,
                    "description" to selectedDescription
                )
                firestore.collection("characters").document(characterId).set(characterData)
                    .addOnSuccessListener {
                        navController.navigate("spells_screen/$charClass/$characterId")
                    }
            }
            Spacer(modifier = Modifier.height(16.dp))
            SectionWithButton("EQUIPMENT:", "EQUIPMENT") {
                navController.navigate("equipment_screen/$characterId")
            }
            Spacer(modifier = Modifier.height(16.dp))
            SectionWithButton("STATS:", "STATS") {
                navController.navigate("stats_screen/$characterId")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val character = PlayableCharacter(
                        characterId = characterId,
                        name = name,
                        characterClass = charClass,
                        race = race,
                        alignment = alignment,
                        hitPoints = 10,
                        attributes = statsViewModel.attributes,
                        level = level.toIntOrNull() ?: 1,
                        physicalDescription = selectedDescription,
                        campaignId = "",
                        inventory = emptyList(),
                        spells = emptyList(),
                        userId = userViewModel.uid
                    )
                    onSave(character)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5F9E89),
                    contentColor = Color.White
                ),
                enabled = name.isNotBlank() && race.isNotBlank() && charClass.isNotBlank() && level.isNotBlank() && alignment.isNotBlank()
            ) {
                Text("SAVE CHARACTER", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CustomInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF8A3A2D)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B2F22),
                unfocusedBorderColor = Color(0xFF7B2F22),
                cursorColor = Color(0xFF7B2F22)
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
            color = Color(0xFF8A3A2D)
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
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7B2F22),
                    unfocusedBorderColor = Color(0xFF7B2F22),
                    cursorColor = Color(0xFF7B2F22)
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
            color = Color(0xFF8A3A2D)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8A3A2D),
                contentColor = Color.White
            )
        ) {
            Text(buttonText.uppercase(), fontWeight = FontWeight.Bold)
        }
    }
}

