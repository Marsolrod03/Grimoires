package com.grimoires.Grimoires.screens.Profile

import HandleMenu
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.ui.models.ProfileState
import com.grimoires.Grimoires.ui.models.ProfileViewModel
import com.grimoires.Grimoires.ui.element_views.UserProfileSection
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val viewModel: ProfileViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val nickname by userViewModel.nickname.collectAsState()
    val currentState by viewModel.state
    val backgroundColor = Color(0xFFF7E9D4)
    val accentColor = Color(0xFFB44B33)
    val textColor = Color(0xFF3B2F2F)

    LaunchedEffect(currentState) {
        if (currentState is ProfileState.LoggedOut) {
            navController.navigate("welcome") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }

    HandleMenu(userViewModel.nickname, navController) { scope, drawerState ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "MY PROFILE",
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
                    actions = {
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.logout),
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.icono1),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Fit
                )

                when (currentState) {
                    is ProfileState.Loading -> CircularProgressIndicator(
                        color = accentColor,
                        strokeWidth = 3.dp
                    )

                    is ProfileState.Success -> {
                        val state = currentState as ProfileState.Success
                        UserProfileSection(
                            userName = state.userName,
                            userEmail = state.userEmail,
                            onNameChange = { viewModel.updateProfileName(it) },
                            accentColor = accentColor
                        )
                    }

                    is ProfileState.Error -> {
                        Text(
                            text = (currentState as ProfileState.Error).message,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}
