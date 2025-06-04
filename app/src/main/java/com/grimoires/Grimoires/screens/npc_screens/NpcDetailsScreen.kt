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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grimoires.Grimoires.domain.model.NonPlayableCharacter
import com.grimoires.Grimoires.ui.models.NpcViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcDetailScreen(
    npcId: String,
    navController: NavController,
    viewModel: NpcViewModel = viewModel()
) {
    var npc by remember { mutableStateOf<NonPlayableCharacter?>(null) }

    LaunchedEffect(npcId) {
        viewModel.getNpc(npcId).collect { npcData ->
            npc = npcData
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(npc?.characterName ?: "NPC Details", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
            if (npc == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                InfoItem("Name", npc!!.characterName)
                InfoItem("Class", npc!!.characterClass)
                InfoItem("Race", npc!!.race)
                InfoItem("Alignment", npc!!.alignment)
                InfoItem("Level", npc!!.level.toString())

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Description",
                    fontWeight = FontWeight.Bold,
                    color = deepBrown,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    npc!!.description,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("$label:", color = deepBrown, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif)
        Text(value, fontFamily = FontFamily.Serif)
    }
}