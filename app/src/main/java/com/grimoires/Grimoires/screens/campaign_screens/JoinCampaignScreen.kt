package com.grimoires.Grimoires.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModel
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
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
                title = { Text("Join Campaign", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB44B33),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
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
                    value = selectedCharacter?.name ?: "Selecciona personaje",
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
                            text = { Text(character.name) },
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
                                campaign = campaign,
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
                Text("JOIN")
            }
        }
    }
}
