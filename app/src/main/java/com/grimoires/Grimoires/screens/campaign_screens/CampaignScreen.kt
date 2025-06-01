package com.grimoires.Grimoires.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.domain.model.Campaign
import com.grimoires.Grimoires.ui.element_views.CampaignCard
import com.grimoires.Grimoires.ui.element_views.CustomDrawerContent
import com.grimoires.Grimoires.ui.element_views.FullScreenLoading
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    viewModel: CampaignViewModel = viewModel()
) {
    val currentUserId = userViewModel.uid
    val currentUserCharacterId = userViewModel.currentCharacterId


    val masteredCampaigns by viewModel.masteredCampaigns.collectAsState()
    val playedCampaigns by viewModel.playedCampaigns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(currentUserId) {
        viewModel.loadMasteredCampaigns(currentUserId)
    }

    LaunchedEffect(currentUserCharacterId) {
        viewModel.loadPlayedCampaigns(currentUserCharacterId)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(currentUserId, currentUserCharacterId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadMasteredCampaigns(currentUserId)
            viewModel.loadPlayedCampaigns(currentUserCharacterId!!)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF5F5F5)
            ) {
                CustomDrawerContent(
                    nickname = "Usuario",
                    onOptionSelected = { option ->
                        when (option) {
                            "MY CHARACTERS" -> navController.navigate("characters")
                            "MY CAMPAIGNS" -> navController.navigate("campaigns")
                            "THE LIBRARY" -> navController.navigate("library")
                            "DICE CALCULATOR" -> navController.navigate("calculator")
                            "profile" -> navController.navigate("userProfileSection")
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Grimoires",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFB44B33),
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
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        var selectedTab by remember { mutableIntStateOf(0) }

                        TabRow(
                            selectedTabIndex = selectedTab,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = Color(0xFFB44B33)
                                )
                            },
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.background(
                                    if (selectedTab == 0) Color(0xFFB44B33) else Color.LightGray
                                ),
                                text = {
                                    Text(
                                        "MASTERED",
                                        color = if (selectedTab == 0) Color.White else Color.Black
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                modifier = Modifier.background(
                                    if (selectedTab == 1) Color(0xFFB44B33) else Color.LightGray
                                ),
                                text = {
                                    Text(
                                        "PLAYED",
                                        color = if (selectedTab == 1) Color.White else Color.Black
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
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.d20),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF8B3A2E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF8B3A2E)
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F9E8F))
        ) {
            Text(text = emptyButtonText, color = Color.White)
        }
    }
}
