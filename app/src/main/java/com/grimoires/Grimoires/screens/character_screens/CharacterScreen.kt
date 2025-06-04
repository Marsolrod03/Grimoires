package com.grimoires.Grimoires.screens.character_screens

import HandleMenu
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.element_views.CharacterCard
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.leafGreen
import com.grimoires.Grimoires.ui.theme.lightTan
import com.grimoires.Grimoires.ui.theme.parchment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    characters: List<PlayableCharacter>,
    onCharacterClick: (PlayableCharacter) -> Unit,
    onAddCharacterClick: () -> Unit,
    nickname: StateFlow<String?>,
    navController: NavHostController
) {
    val backgroundColor = lightTan
    val viewModel: PlayableCharacterViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    var characterToDelete by remember { mutableStateOf<PlayableCharacter?>(null) }


    LaunchedEffect(Unit) {
        userViewModel.uid?.let { viewModel.loadCharactersForUser(it.toString()) }
    }


    HandleMenu(nickname, navController) { scope, drawerState ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "MY CHARACTERS",
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
                )
            },
            containerColor = backgroundColor
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (characters.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.d20),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NO CHARACTERS YET",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = parchment
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddCharacterClick,
                            colors = ButtonDefaults.buttonColors(leafGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "ADD NEW CHARACTER")
                        }
                    }
                } else {
                    LazyColumn {
                        items(characters) { character ->
                            CharacterCard(
                                character = character,
                                onClick = { onCharacterClick(character) },
                                onLongClick = { characterToDelete = character }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = onAddCharacterClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(leafGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("ADD NEW CHARACTER")
                            }
                        }
                    }
                    characterToDelete?.let { character ->
                        AlertDialog(
                            onDismissRequest = { characterToDelete = null },
                            title = { Text("Do you want to delete this character?") },
                            text = { Text("You cant undo this action.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.deleteCharacter(
                                        characterId = character.characterId,
                                        onSuccess = {
                                            userViewModel.uid?.let {
                                                viewModel.loadCharactersForUser(
                                                    it.toString()
                                                )
                                            }
                                            characterToDelete = null
                                        },
                                        onError = { error ->
                                            println("Error: $error")
                                            characterToDelete = null
                                        }
                                    )
                                }) {
                                    Text("YES")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { characterToDelete = null }) {
                                    Text("NO")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
