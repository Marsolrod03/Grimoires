package com.grimoires.Grimoires.screens.campaign_screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.grimoires.Grimoires.screens.npc_screens.InfoItem
import com.grimoires.Grimoires.ui.element_views.CampaignCharacterCard
import com.grimoires.Grimoires.ui.models.NotesViewModel
import com.grimoires.Grimoires.ui.models.PublicNotesSection
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan
import com.grimoires.Grimoires.ui.theme.oak
import com.grimoires.Grimoires.viewmodel.CampaignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
    navController: NavController,
    campaignId: String,
    notesViewModel: NotesViewModel,
    campaignViewModel: CampaignViewModel,
    userViewModel: UserViewModel
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val campaign by campaignViewModel.getCampaign(campaignId).collectAsState(initial = null)
    val participants by campaignViewModel.participants.collectAsState()
    val npcs by campaignViewModel.npcs.collectAsState()


    var selectedTab by remember { mutableIntStateOf(0) }

    val isMaster = remember(campaign) {
        campaign?.masterID == currentUserId
    }


    LaunchedEffect(campaignId) {
        campaignViewModel.loadCampaignParticipants(campaignId)
        campaignViewModel.loadCampaignNpcs(campaignId)
        notesViewModel.loadNotes(campaignId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        campaign?.title ?: "",
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
                val tabTitles = listOf("PUBLIC", "PRIVATE", "CHARACTERS")
                TabRow(
                    selectedTabIndex = selectedTab,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = deepBrown
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = if (selectedTab == index) Color.White else lightTan
                                )
                            },
                            modifier = Modifier.background(if (selectedTab == index) oak else deepBrown)
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> PublicInfoTab(campaign, notesViewModel, isMaster, currentUserId, campaignId)
                1 -> {
                    if (isMaster) {
                        PrivateMasterTab(campaign, participants, npcs, navController, campaignId)
                    } else {
                        PublicInfoTab(campaign, notesViewModel, isMaster, currentUserId, campaignId)
                    }
                }
                2 -> {
                    if (isMaster) {
                        CharactersTab(participants, navController)
                    } else {
                        PublicInfoTab(campaign, notesViewModel, isMaster, currentUserId, campaignId)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun PublicInfoTab(
    campaign: Campaign?,
    notesViewModel: NotesViewModel,
    isMaster: Boolean,
    userId: String,
    campaignId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionTitle("CAMPAIGN INFORMATION")
        InfoItem("Title", campaign?.title ?: "")
        InfoItem("Description", campaign?.description ?: "")

        Spacer(modifier = Modifier.height(24.dp))

        PublicNotesSection(
            campaignId = campaignId,
            userId = userId,
            isMaster = isMaster,
            notesViewModel = notesViewModel
        )
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = deepBrown)
        ) {
            Text("MANAGE NPCs")
        }
    }
}

@Composable
fun CharactersTab(
    participants: List<Participant>,
    navController: NavController
) {

    val players = participants.filter { !it.isMaster }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("CHARACTERS")

        if (players.isEmpty()) {
            Text("No characters yet", modifier = Modifier.padding(16.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                players.forEach { participant ->
                    CampaignCharacterCard(
                        participant = participant,
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
fun ParticipantItem(participant: Participant) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        if (participant.isMaster) {
            Text(
                text = "• ${participant.nickname} (Master)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = deepBrown
            )
        } else {
            Text(
                text = "• ${participant.nickname} (${participant.characterName})",
                fontFamily = FontFamily.Serif,
                color = if (participant.characterName == "Sin personaje") Color.Red else Color.Unspecified
            )
        }
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