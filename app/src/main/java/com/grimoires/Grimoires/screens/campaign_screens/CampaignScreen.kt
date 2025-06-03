package com.grimoires.Grimoires.ui.screen

import HandleMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.domain.model.Campaign
import com.grimoires.Grimoires.ui.element_views.CampaignCard
import com.grimoires.Grimoires.ui.element_views.FullScreenLoading
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.leafGreen
import com.grimoires.Grimoires.ui.theme.lightTan
import com.grimoires.Grimoires.ui.theme.oak
import com.grimoires.Grimoires.ui.theme.parchment
import com.grimoires.Grimoires.viewmodel.CampaignViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignScreen(
    navController: NavHostController,
    nickname: String,
    userViewModel: UserViewModel = viewModel(),
    viewModel: CampaignViewModel = viewModel()
) {
    val currentUserId = userViewModel.uid ?: ""
    val masteredCampaigns by viewModel.masteredCampaigns.collectAsState()
    val playedCampaigns by viewModel.playedCampaigns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) {
            viewModel.loadMasteredCampaigns(currentUserId)
            viewModel.loadPlayedCampaigns(currentUserId)
        }
    }



    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    HandleMenu(nickname, navController) { scope, drawerState ->
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "GRIMOIRES",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 24.sp,
                                color = Color.White,
                                shadow = Shadow(blurRadius = 4f, color = Color.Black)
                            )
                        )

                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
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
            },
            content = { innerPadding ->
                if (isLoading) {
                    FullScreenLoading()
                } else {
                    Column(
                        modifier = Modifier
                            .background(lightTan)
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        var selectedTab by remember { mutableIntStateOf(0) }

                        TabRow(
                            selectedTabIndex = selectedTab,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = deepBrown
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.background(
                                    if (selectedTab == 0) oak else deepBrown
                                ),
                                text = {
                                    Text(
                                        "MASTERED",
                                        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                                        color = if (selectedTab == 0) Color.White else parchment
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                modifier = Modifier.background(
                                    if (selectedTab == 1) oak else deepBrown
                                ),
                                text = {
                                    Text(
                                        "PLAYED",
                                        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                                        color = if (selectedTab == 1) Color.White else parchment
                                    )
                                }
                            )
                        }

                        when (selectedTab) {
                            0 -> CampaignList(
                                campaigns = masteredCampaigns,
                                onCampaignClick = { campaign ->
                                    navController.navigate("campaign_detail/${campaign.idCampaign}")
                                },
                                onAddClick = { navController.navigate("createCampaign") },
                                emptyMessage = "No mastered campaigns",
                                emptyButtonText = "CREATE CAMPAIGN"
                            )

                            1 -> CampaignList(
                                campaigns = playedCampaigns,
                                onCampaignClick = { campaign ->
                                    navController.navigate("campaign_detail/${campaign.idCampaign}")
                                },
                                onAddClick = { navController.navigate("joinCampaign") },
                                emptyMessage = "No campaigns played",
                                emptyButtonText = "JOIN CAMPAIGN"
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun CampaignList(
    campaigns: List<Campaign>,
    onCampaignClick: (Campaign) -> Unit,
    onAddClick: () -> Unit,
    emptyMessage: String,
    emptyButtonText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (campaigns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.d20),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.titleMedium,
                        color = deepBrown
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(campaigns) { campaign ->
                    CampaignCard(campaign = campaign, onClick = { onCampaignClick(campaign) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = leafGreen)
        ) {
            Text(text = emptyButtonText, color = Color.White)
        }
    }
}
