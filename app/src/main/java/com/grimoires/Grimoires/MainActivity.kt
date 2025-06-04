package com.grimoires.Grimoires


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
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
import com.grimoires.Grimoires.screens.campaign_screens.CampaignDetailScreen
import com.grimoires.Grimoires.screens.campaign_screens.CreateCampaignScreen
import com.grimoires.Grimoires.screens.catalog_screens.EquipmentScreen
import com.grimoires.Grimoires.screens.catalog_screens.EquipmentSelectionScreen
import com.grimoires.Grimoires.screens.catalog_screens.SpellsScreen
import com.grimoires.Grimoires.screens.catalog_screens.SpellsSelectionScreen
import com.grimoires.Grimoires.screens.character_screens.AddCharacterScreen
import com.grimoires.Grimoires.screens.character_screens.CharacterScreen
import com.grimoires.Grimoires.screens.character_screens.CharacterSheetScreen
import com.grimoires.Grimoires.screens.character_screens.EditCharacterScreen
import com.grimoires.Grimoires.screens.character_screens.StatsScreen
import com.grimoires.Grimoires.screens.home_screen.HomeScreenWithDrawer
import com.grimoires.Grimoires.screens.home_screen.WebViewScreen
import com.grimoires.Grimoires.screens.library_screen.ClassDetailScreen
import com.grimoires.Grimoires.screens.library_screen.ItemDetailScreen
import com.grimoires.Grimoires.screens.library_screen.LibraryScreen
import com.grimoires.Grimoires.screens.library_screen.RaceDetailScreen
import com.grimoires.Grimoires.screens.library_screen.SpellDetailScreen
import com.grimoires.Grimoires.screens.npc_screens.NpcCreationScreen
import com.grimoires.Grimoires.screens.npc_screens.NpcDetailScreen
import com.grimoires.Grimoires.screens.npc_screens.NpcManagementScreen
import com.grimoires.Grimoires.ui.element_views.FullScreenLoading
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.LoginViewModel
import com.grimoires.Grimoires.ui.models.NotesViewModel
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.models.StatsViewModel
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.screen.CampaignScreen
import com.grimoires.Grimoires.ui.screen.JoinCampaignScreen
import com.grimoires.Grimoires.viewmodel.CampaignViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModelFactory


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
    val startDestination = if (Firebase.auth.currentUser != null) "home" else "welcome"
    val loginViewModel: LoginViewModel = viewModel()
    val characterViewModel: PlayableCharacterViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val statsViewModel: StatsViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel()
    val campaignViewModel: CampaignViewModel =
        viewModel(factory = CampaignViewModelFactory())
    val notesViewModel: NotesViewModel = viewModel()
    val nickname by userViewModel.nickname.collectAsState()




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
        startDestination = if (Firebase.auth.currentUser != null) "home" else "welcome"
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

        composable(
            route = "webView/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            WebViewScreen(url = url)
        }

        composable("characters") {
            val characters by characterViewModel.userCharacters.collectAsState()
            val nickname = userViewModel.nickname
            val uid by userViewModel.uid.collectAsState()

            LaunchedEffect(uid) {
                uid?.let { characterViewModel.loadCharactersForUser(it) }
            }

            CharacterScreen(
                characters = characters,
                onCharacterClick = { character -> navController.navigate("characterDetail/${character.characterId}") },
                onAddCharacterClick = { navController.navigate("addCharacter") },
                nickname = nickname,
                navController = navController
            )
        }

        composable(
            "characterDetail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val characters by characterViewModel.userCharacters.collectAsState()
            val character = characters.find { it.characterId == characterId }

            if (character != null) {
                CharacterSheetScreen(
                    character = character,
                    catalogViewModel = catalogViewModel,
                    statsViewModel = statsViewModel,
                    navController = navController,
                    onEditClick = { /*...*/ }
                )
            } else {
                FullScreenLoading()
            }
        }

        composable("campaign_detail/{campaignId}") { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            if (campaignId.isNotEmpty()) {
                CampaignDetailScreen(
                    navController = navController,
                    campaignId = campaignId,
                    campaignViewModel = campaignViewModel,
                    userViewModel = userViewModel,
                    notesViewModel = notesViewModel
                )
            } else {
                Text("Error: ID de campaña inválido")
            }
        }
        composable("addCharacter") {
            AddCharacterScreen(
                navController = navController,
                userViewModel = userViewModel,
                statsViewModel = statsViewModel,
                onSave = { character, selectedSpells, selectedItems ->
                    characterViewModel.addCharacterToFirestore(
                        character,
                        onSuccess = { characterId ->
                            val spellIds = selectedSpells.map { it.spellId }
                            val itemIds = selectedItems.map { it.itemId }
                            characterViewModel.saveSelectedSpells(characterId, spellIds)
                            characterViewModel.saveSelectedItems(characterId, itemIds)

                            navController.navigate("characters") {
                                popUpTo("addCharacter") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onError = { /* handle error */ }
                    )
                }
            )
        }

        composable(
            "stats_screen/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""

            StatsScreen(
                navController = navController,
                statsViewModel = statsViewModel,
                characterId = characterId
            )
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
            SpellsSelectionScreen(
                navController = navController,
                characterClass = characterClass,
                characterId = characterId
            )
        }

        composable("characterSheet/{characterId}") { backStackEntry ->
            val characterId =
                backStackEntry.arguments?.getString("characterId") ?: return@composable

            val playableCharacterViewModel: PlayableCharacterViewModel = viewModel()
            val character by playableCharacterViewModel.currentCharacter.collectAsState()
            val isLoading by playableCharacterViewModel.isLoading.collectAsState()

            LaunchedEffect(characterId) {
                playableCharacterViewModel.loadCharacterById(characterId)
            }

            if (isLoading) {
                Text("Loading character...")
            } else if (character != null) {
                CharacterSheetScreen(
                    character = character!!,
                    catalogViewModel = catalogViewModel,
                    statsViewModel = statsViewModel,
                    onEditClick = { /* lógica de edición */ },
                    navController = navController
                )
            } else {
                Text("Character not found.")
            }
        }

        composable("npcs/{campaignId}") { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            NpcManagementScreen(campaignId, navController)
        }

        composable("create_npc/{campaignId}") { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            NpcCreationScreen(campaignId, navController)
        }

        composable("npc_detail/{npcId}") { backStackEntry ->
            val npcId = backStackEntry.arguments?.getString("npcId") ?: ""
            NpcDetailScreen(npcId, navController)
        }

        composable(
            "equipment_screen/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            EquipmentSelectionScreen(navController, characterId)
        }

        composable("calculator") {
            DiceCalculatorScreen(navController)
        }

        composable("library") {
            LibraryScreen(
                navController = navController,
                nickname = userViewModel.nickname,
                viewModel = catalogViewModel
            )
        }

        composable("detail/race/{raceId}") { backStackEntry ->
            val raceId = backStackEntry.arguments?.getString("raceId") ?: ""
            val races by catalogViewModel.races.collectAsState()
            val race = races.find { it.raceId == raceId }

            race?.let {
                RaceDetailScreen(race = it) {
                    navController.popBackStack()
                }
            }
        }

        composable("detail/class/{classId}") { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: ""
            val classes by catalogViewModel.classes.collectAsState()
            val charClass = classes.find { it.classId == classId }

            charClass?.let {
                ClassDetailScreen(characterClass = it) {
                    navController.popBackStack()
                }
            }
        }

        composable("edit_character/{characterId}") { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            EditCharacterScreen(characterId = characterId, navController = navController)
        }



        composable("detail/item/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val items by catalogViewModel.items.collectAsState()
            val item = items.find { it.itemId == itemId }

            item?.let {
                ItemDetailScreen(item = it) {
                    navController.popBackStack()
                }
            }
        }

        composable("equipment_select/{characterId}") { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            EquipmentSelectionScreen(navController = navController, characterId = characterId)
        }

        composable("spells_select/{characterId}/{characterClass}") { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            val characterClass = backStackEntry.arguments?.getString("characterClass") ?: ""
            SpellsSelectionScreen(
                navController = navController,
                characterId = characterId,
                characterClass = characterClass
            )
        }


        composable("detail/spell/{spellId}") { backStackEntry ->
            val spellId = backStackEntry.arguments?.getString("spellId") ?: ""
            val spells by catalogViewModel.spells.collectAsState()
            val spell = spells.find { it.spellId == spellId }

            spell?.let {
                SpellDetailScreen(spell = it) {
                    navController.popBackStack()
                }
            }
        }

        composable("campaigns") {
            CampaignScreen(
                navController = navController,
                userViewModel = userViewModel,
                viewModel = campaignViewModel,
                nickname = userViewModel.nickname
            )
        }

        composable("createCampaign") {
            CreateCampaignScreen(
                navController = navController,
                campaignViewModel = campaignViewModel
            )
        }

        composable("joinCampaign") {
            JoinCampaignScreen(
                navController = navController,
                campaignViewModel = campaignViewModel,
                userViewModel = userViewModel
            )
        }

        composable(
            "inventoryScreen/{characterName}",
            arguments = listOf(navArgument("characterName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId by userViewModel.uid.collectAsState()

            EquipmentScreen(
                characterId = userId ?: "",
                viewModel = characterViewModel,
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }


        composable(
            "spellsScreen/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
            SpellsScreen(
                characterId = characterId,
                viewModel = characterViewModel,
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}


