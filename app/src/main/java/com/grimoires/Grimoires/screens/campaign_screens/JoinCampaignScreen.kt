package com.grimoires.Grimoires.screens.campaign_screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModel

@Composable
fun JoinCampaignScreen(
    navController: NavController,
    campaignViewModel: CampaignViewModel = viewModel(),
    characterViewModel: PlayableCharacterViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(currentUserId) {
        characterViewModel.loadCharactersForUser(currentUserId)
    }

    var code by remember { mutableStateOf("") }
    var selectedCharacter by remember { mutableStateOf<PlayableCharacter?>(null) }
    val characterList by characterViewModel.characters.collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF1E6))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Join Campaign",
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7C3A2D))
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "CHOOSE CHARACTER TO JOIN WITH:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF7C3A2D)
        )

        Box {
            OutlinedTextField(
                value = selectedCharacter?.name ?: "Choose Character",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                characterList.forEach { character ->
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "PASTE CODE:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF7C3A2D)
        )

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Black)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (selectedCharacter == null || code.isBlank()) {
                    Toast.makeText(context, "Select a character and enter code", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                campaignViewModel.getCampaignByCode(code,
                    onSuccess = { campaign ->
                        if (campaign.masterID == currentUserId) {
                            Toast.makeText(context, "You are the master of this campaign", Toast.LENGTH_SHORT).show()
                        } else {
                            campaignViewModel.joinCampaign(
                                campaignId = campaign.idCampaign,
                                characterId = selectedCharacter!!.characterId,
                                onSuccess = {
                                    Toast.makeText(context, "Joined successfully!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    onError = {
                        Toast.makeText(context, "Campaign not found", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D9B82)),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("JOIN", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
