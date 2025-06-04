package com.grimoires.Grimoires.screens.npc_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grimoires.Grimoires.screens.campaign_screens.NpcCard
import com.grimoires.Grimoires.ui.models.NpcViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grimoires.Grimoires.ui.theme.lightTan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcManagementScreen(
    campaignId: String,
    navController: NavController,
    viewModel: NpcViewModel = viewModel()
) {
    val npcs by viewModel.getCampaignNpcs(campaignId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NPC MANAGEMENT",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_npc/$campaignId") },
                containerColor = deepBrown
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add NPC", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(lightTan)
                .padding(16.dp)
        ) {
            if (npcs.isEmpty()) {
                Text(
                    "No NPCs created yet",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn {
                    items(npcs) { npc ->
                        NpcCard(
                            npc = npc,
                            onClick = {
                                navController.navigate("npc_detail/${npc.characterId}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}