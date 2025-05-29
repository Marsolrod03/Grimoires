package com.grimoires.Grimoires.screens.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.ui.element_views.CustomDrawerContent
import com.grimoires.Grimoires.ui.element_views.MenuAdView
import com.grimoires.Grimoires.ui.models.HomeScreenViewModel
import com.grimoires.Grimoires.ui.models.UserViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenWithDrawer(navController: NavHostController) {
    val viewModel: HomeScreenViewModel = viewModel()
    val ads by viewModel.ads

    Color(0xFFB44B33)

    val userViewModel: UserViewModel = viewModel()
    val nickname = userViewModel.nickname
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawerContent(
                nickname = nickname,
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
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Grimoires", color = Color.White) },
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
                        containerColor = Color(0xFFB44B33),
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7E9D4))
            ) {
                items(ads) { ad ->
                    MenuAdView(ad)
                }
            }
        }
    }
}