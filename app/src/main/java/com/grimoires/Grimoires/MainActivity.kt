package com.grimoires.Grimoires

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grimoires.Grimoires.screens.Profile.ProfileScreen
import com.grimoires.Grimoires.screens.authentication_screens.ForgotPasswordScreen
import com.grimoires.Grimoires.screens.authentication_screens.LoginScreen
import com.grimoires.Grimoires.screens.authentication_screens.SignUpScreen
import com.grimoires.Grimoires.screens.authentication_screens.WelcomeScreen
import com.grimoires.Grimoires.screens.home_screen.HomeScreenWithDrawer
import com.grimoires.Grimoires.ui.models.LoginViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel: LoginViewModel = viewModel()

    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn.value) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (Firebase.auth.currentUser != null) "home" else "welcome"
    ) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("register") { SignUpScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("home") { HomeScreenWithDrawer(navController) }
        composable("profile") { ProfileScreen(navController) }

    }
}