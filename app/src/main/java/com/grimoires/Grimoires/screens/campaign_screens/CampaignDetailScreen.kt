package com.grimoires.Grimoires.screens.campaign_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grimoires.Grimoires.domain.model.User

import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
    navController: NavController,
    campaignId: String,
    campaignViewModel: CampaignViewModel,
    userViewModel: UserViewModel
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val campaign by campaignViewModel.getCampaign(campaignId).collectAsState(initial = null)
    val isMaster = campaign?.masterID == currentUserId

    LaunchedEffect(campaignId) {
        campaignViewModel.loadCampaignParticipants(campaignId)
    }
    val participants by campaignViewModel.participants.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(campaign?.title ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle("PUBLIC INFO")
            InfoItem("Nombre", campaign?.title ?: "")
            InfoItem("Género", campaign?.genre ?: "")
            InfoItem("Descripción", campaign?.description ?: "")

            Spacer(modifier = Modifier.height(24.dp))

            if (isMaster) {
                SectionTitle("PRIVATE")
                InfoItem("Código", campaign?.accessCode ?: "")

                Spacer(modifier = Modifier.height(16.dp))

                Text("Personajes:", fontWeight = FontWeight.Bold)
                participants.forEach { participant ->
                    ParticipantItem(participant)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("notes/$campaignId") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("NOTAS Y RECORDATORIOS")
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF7C3A2D),
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Divider()
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Medium)
        Text(text = value)
    }
}

@Composable
fun ParticipantItem(participant: User) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "• ${participant.nickname}")
    }
}