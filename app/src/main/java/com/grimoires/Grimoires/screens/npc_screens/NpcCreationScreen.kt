package com.grimoires.Grimoires.screens.npc_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grimoires.Grimoires.domain.model.NonPlayableCharacter
import com.grimoires.Grimoires.ui.models.NpcViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grimoires.Grimoires.ui.theme.oak


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcCreationScreen(
    campaignId: String,
    navController: NavController,
    viewModel: NpcViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var characterClass by remember { mutableStateOf("") }
    var race by remember { mutableStateOf("") }
    var alignment by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(1) }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CREATE NPC",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = Color.White,
                            shadow = Shadow(blurRadius = 4f, color = Color.Black)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(lightTan)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", fontFamily = Serif) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = characterClass,
                onValueChange = { characterClass = it },
                label = { Text("Class", fontFamily = Serif) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = race,
                onValueChange = { race = it },
                label = { Text("Race", fontFamily = Serif) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = alignment,
                onValueChange = { alignment = it },
                label = { Text("Alignment", fontFamily = Serif) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Level: ", color = deepBrown)

                Slider(
                    value = level.toFloat(),
                    onValueChange = { level = it.toInt() },
                    valueRange = 1f..20f,
                    steps = 18,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = oak,
                        activeTrackColor = deepBrown,
                        inactiveTrackColor = Color.LightGray
                    )
                )

                Text(
                    "$level",
                    color = deepBrown,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", fontFamily = Serif) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val newNpc = NonPlayableCharacter(
                        characterName = name,
                        characterClass = characterClass,
                        race = race,
                        alignment = alignment,
                        level = level,
                        masterId = viewModel.currentUserId,
                        description = description
                    )
                    viewModel.createNpc(newNpc, campaignId) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = deepBrown)
            ) {
                Text("CREATE NPC", color = Color.White)
            }
        }
    }
}