package com.grimoires.Grimoires.screens.character_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.leafGreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterScreen(
    characterId: String,
    viewModel: PlayableCharacterViewModel = viewModel(),
    navController: NavController
) {
    val character by viewModel.currentCharacter.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.loadCharacterById(characterId)
    }

    if (character == null) {
        Text("Loading...")
        return
    }

    var name by remember { mutableStateOf(character!!.characterName) }
    var charClass by remember { mutableStateOf(character!!.characterClass) }
    var race by remember { mutableStateOf(character!!.race) }
    var alignment by remember { mutableStateOf(character!!.alignment) }
    var description by remember { mutableStateOf(character!!.physicalDescription) }
    var level by remember { mutableStateOf(character!!.level.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Character", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = charClass,
                onValueChange = { charClass = it },
                label = { Text("Class") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = race,
                onValueChange = { race = it },
                label = { Text("Race") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = alignment,
                onValueChange = { alignment = it },
                label = { Text("Alignment") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = level,
                onValueChange = { level = it },
                label = { Text("Level") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedCharacter = character!!.copy(
                        characterName = name,
                        characterClass = charClass,
                        race = race,
                        alignment = alignment,
                        physicalDescription = description,
                        level = level.toIntOrNull() ?: character!!.level
                    )

                    viewModel.addCharacterToFirestore(
                        updatedCharacter,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = {

                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = leafGreen)
            ) {
                Text("Save Changes", color = Color.White)
            }
        }
    }
}
