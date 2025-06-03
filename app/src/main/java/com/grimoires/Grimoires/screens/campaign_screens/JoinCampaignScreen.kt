package com.grimoires.Grimoires.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModel
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinCampaignScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    playableCharacterViewModel: PlayableCharacterViewModel = viewModel(),
    campaignViewModel: CampaignViewModel = viewModel()
) {
    val characters by playableCharacterViewModel.userCharacters.collectAsState()
    val currentUserId = userViewModel.uid ?: ""
    val campaignCode = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    var expanded by remember { mutableStateOf(false) }
    var selectedCharacter by remember { mutableStateOf<PlayableCharacter?>(null) }

    LaunchedEffect(userViewModel.uid) {
        userViewModel.uid?.let { playableCharacterViewModel.loadCharactersForUser(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "JOIN CAMPAIGN",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = Color.White,
                            shadow = Shadow(blurRadius = 4f, color = Color.Black)
                        )
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
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .background(lightTan)
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCharacter?.characterName ?: "Selecciona personaje",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Personaje") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    characters.forEach { character ->
                        DropdownMenuItem(
                            text = { Text(character.characterName) },
                            onClick = {
                                selectedCharacter = character
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = campaignCode.value,
                onValueChange = { campaignCode.value = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    if (campaignCode.value.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor ingresa un código de campaña")
                        }
                        return@Button
                    }

                    campaignViewModel.getCampaignByAccessCode(
                        accessCode = campaignCode.value,
                        onSuccess = { campaign ->
                            campaignViewModel.joinCampaign(
                                accessCode = campaignCode.value,
                                characterName = selectedCharacter?.characterName ?: "",
                                userId = currentUserId,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Succesfully Joined!")
                                        navController.popBackStack()
                                    }
                                },
                                onError = { error ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(error)
                                    }
                                }
                            )
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = deepBrown,
                    contentColor = Color.White
                )
            ) {
                Text("JOIN",fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            }
        }
    }
}
