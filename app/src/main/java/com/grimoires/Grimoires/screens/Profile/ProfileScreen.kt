package com.grimoires.Grimoires.screens.Profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.ui.models.ProfileState
import com.grimoires.Grimoires.ui.models.ProfileViewModel
import com.grimoires.Grimoires.ui.element_views.UserProfileSection



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val viewModel: ProfileViewModel = viewModel()
    val currentState by viewModel.state
    val backgroundColor = Color(0xFFF7E9D4)
    val accentColor = Color(0xFFB44B33)

    LaunchedEffect(currentState) {
        if (currentState is ProfileState.LoggedOut) {
            navController.navigate("welcome") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = accentColor
                ),
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        color = accentColor,
                        strokeWidth = 2.dp
                    )
                }
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
