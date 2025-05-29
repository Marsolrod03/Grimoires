package com.grimoires.Grimoires.screens.character_screens

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
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.element_views.CharacterCard
import com.grimoires.Grimoires.ui.element_views.CustomDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    characters: List<PlayableCharacter>,
    onCharacterClick: (PlayableCharacter) -> Unit,
    onAddCharacterClick: () -> Unit,
    nickname: String,
    navController: NavHostController
) {
    val backgroundColor = Color(0xFFF7EDDF)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawerContent(
                nickname = nickname,
                onOptionSelected = { option ->
                    val route = when (option) {
                        "MY CHARACTERS" -> "character"
                        "MY CAMPAIGNS" -> "campaigns"
                        "THE LIBRARY" -> "library"
                        "DICE CALCULATOR" -> "calculator"
                        "profile" -> "userProfileSection"
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it)
                        scope.launch { drawerState.close() }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Characters", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF8B3A2E))
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
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NO CHARACTERS YET",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF7C3A2D)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddCharacterClick,
                            colors = ButtonDefaults.buttonColors(Color(0xFF5F9E8F)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "ADD NEW CHARACTER")
                        }
                    }
                } else {
                    LazyColumn {
                        items(characters) { character ->
                            CharacterCard(character = character, onClick = { onCharacterClick(character) })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = onAddCharacterClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(Color(0xFF5F9E8F)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("ADD NEW CHARACTER")
                            }
                        }
                    }
                }
            }
        }
    }
}
