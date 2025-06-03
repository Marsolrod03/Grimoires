package com.grimoires.Grimoires.screens.campaign_screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grimoires.Grimoires.domain.model.Campaign
import com.grimoires.Grimoires.domain.model.NonPlayableCharacter
import com.grimoires.Grimoires.domain.model.Participant
import com.grimoires.Grimoires.domain.model.User
import com.grimoires.Grimoires.ui.element_views.CampaignCharacterCard
import com.grimoires.Grimoires.ui.element_views.CharacterCard
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan
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
    val participants by campaignViewModel.participants.collectAsState()
    val npcs by campaignViewModel.npcs.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(campaignId) {
        campaignViewModel.loadCampaignParticipants(campaignId)
        campaignViewModel.loadCampaignNpcs(campaignId)
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        campaign?.title ?: "Loading...",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = deepBrown,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(lightTan)
        ) {
            if (isMaster) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "PUBLIC",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "PRIVATE",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "CHARACTERS",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> PublicInfoTab(campaign)
                1 -> PrivateMasterTab(campaign, participants, npcs, navController, campaignId)
                2 -> CharactersTab(participants, navController, campaignId)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("notes/$campaignId") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("NOTES")
            }
        }
    }
}

@Composable
fun PublicInfoTab(campaign: Campaign?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("CAMPAIGN INFORMATION")
        InfoItem("Name", campaign?.title ?: "")
        InfoItem("Genre", campaign?.genre ?: "")
        InfoItem("Description", campaign?.description ?: "")
    }
}

@Composable
fun PrivateMasterTab(
    campaign: Campaign?,
    participants: List<Participant>,
    npcs: List<NonPlayableCharacter>,
    navController: NavController,
    campaignId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("PRIVATE INFORMATION")
        InfoItem("Access Code: ", campaign?.accessCode ?: "")

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("PARTICIPANTS")
        if (participants.isEmpty()) {
            Text("No participants", modifier = Modifier.padding(8.dp))
        } else {
            participants.forEach { participant ->
                ParticipantItem(participant)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("NON-PLAYER CHARACTERS (NPCs)")
        if (npcs.isEmpty()) {
            Text("No NPCs created", modifier = Modifier.padding(8.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                npcs.forEach { npc ->
                    NpcCard(
                        npc = npc,
                        onClick = {
                            navController.navigate("npc_detail/${npc.characterId}/$campaignId")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("npcs/$campaignId") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("MANAGE NPCs")
        }
    }
}

@Composable
fun CharactersTab(
    participants: List<Participant>,
    navController: NavController,
    campaignId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("CHARACTERS")

        if (participants.isEmpty()) {
            Text("No characters yet", modifier = Modifier.padding(16.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                participants.forEach { participant ->
                    CampaignCharacterCard(
                        characterName = participant.characterName,
                        playerName = participant.nickname,
                        onClick = {
                            navController.navigate("characterSheet/${participant.characterID}")

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = deepBrown,
        fontFamily = FontFamily.Serif,
        letterSpacing = 1.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Divider(color = deepBrown, thickness = 1.dp)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif)
        Text(text = value, fontFamily = FontFamily.Serif)
    }
}

@Composable
fun ParticipantItem(participant: Participant) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "• ${participant.nickname} (${participant.characterName})",
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun NpcCard(
    npc: NonPlayableCharacter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, deepBrown),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = npc.characterName,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = deepBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${npc.characterClass} (Level ${npc.level})",
                    fontFamily = FontFamily.Serif,
                    color = deepBrown,
                    fontSize = 14.sp
                )
            }
        }
    }
}