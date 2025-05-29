package com.grimoires.Grimoires

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grimoires.Grimoires.screens.Profile.ProfileScreen
import com.grimoires.Grimoires.screens.authentication_screens.ForgotPasswordScreen
import com.grimoires.Grimoires.screens.authentication_screens.LoginScreen
import com.grimoires.Grimoires.screens.authentication_screens.SignUpScreen
import com.grimoires.Grimoires.screens.authentication_screens.WelcomeScreen
import com.grimoires.Grimoires.screens.calculator.DiceCalculatorScreen
import com.grimoires.Grimoires.screens.character_screens.AddCharacterScreen
import com.grimoires.Grimoires.screens.character_screens.CharacterSheetScreen
import com.grimoires.Grimoires.screens.character_screens.CharacterScreen
import com.grimoires.Grimoires.screens.catalog_screens.EquipmentScreen
import com.grimoires.Grimoires.screens.catalog_screens.SpellsScreen
import com.grimoires.Grimoires.screens.character_screens.StatsScreen
import com.grimoires.Grimoires.screens.home_screen.HomeScreenWithDrawer
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.LoginViewModel
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.models.StatsViewModel
import com.grimoires.Grimoires.ui.models.UserViewModel


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
    val loginViewModel: LoginViewModel = viewModel()
    val characterViewModel: PlayableCharacterViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val statsViewModel: StatsViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel()

    LaunchedEffect(loginViewModel.isLoggedIn.value) {
        if (loginViewModel.isLoggedIn.value) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (Firebase.auth.currentUser !=null) "home" else "welcome"
    ) {
        composable("welcome") { WelcomeScreen(navController) }

        composable("login") {
            LoginScreen(navController, loginViewModel)
        }

        composable("register") { SignUpScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("home") { HomeScreenWithDrawer(navController) }

        composable("userProfileSection") {
            ProfileScreen(navController)
        }


        composable("characters") {
            val viewModel: PlayableCharacterViewModel = viewModel()
            val characters by viewModel.characters.collectAsState()
            val nickname = userViewModel.nickname

            LaunchedEffect(Unit) {
                viewModel.loadCharacters()
            }

            CharacterScreen(
                characters = characters,
                onCharacterClick = { character ->
                    navController.navigate("characterDetail/${character.characterId}")
                },
                onAddCharacterClick = {
                    navController.navigate("addCharacter")
                },
                nickname = nickname,
                navController = navController
            )
        }


        composable(
            "characterDetail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val viewModel: PlayableCharacterViewModel = viewModel()

            val characters by viewModel.characters.collectAsState()
            val character = characters.find { it.characterId == characterId }

            if (character != null) {
                CharacterSheetScreen(
                    character = character,
                    catalogViewModel = catalogViewModel,
                    onEditClick = {

                    }
                )
            }
        }


        composable("addCharacter") {
            AddCharacterScreen(
                navController = navController,
                userViewModel = userViewModel,
                statsViewModel = statsViewModel,
                onSave = { character ->
                    characterViewModel.addCharacter(character)
                    navController.popBackStack()
                }
            )
        }

        composable(
            "stats_screen/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""

            StatsScreen(navController = navController, statsViewModel = statsViewModel, characterId = characterId)
        }

        composable(
            "spells_screen/{characterClass}/{characterId}",
            arguments = listOf(
                navArgument("characterClass") { type = NavType.StringType },
                navArgument("characterId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val characterClass = backStackEntry.arguments?.getString("characterClass") ?: ""
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            SpellsScreen(navController = navController, characterClass = characterClass, characterId = characterId)
        }

        composable(
            "equipment_screen/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            EquipmentScreen(navController, characterId)
        }

        composable("calculator") {
            DiceCalculatorScreen()
        }
    }
}
