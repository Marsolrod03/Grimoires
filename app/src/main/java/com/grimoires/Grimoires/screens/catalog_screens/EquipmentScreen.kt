package com.grimoires.Grimoires.screens.catalog_screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.parchment
import com.grimoires.Grimoires.ui.theme.oak


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentScreen(
    characterId: String,
    viewModel: PlayableCharacterViewModel,
    navController: NavController,
    onBackClick: () -> Unit
) {
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var characterName by remember { mutableStateOf("Character") }

    LaunchedEffect(characterId) {
        viewModel.loadCharacterById(characterId)
        viewModel.loadItemsByIds(characterId) { loadedItems ->
            items = loadedItems
        }
    }

    val character by viewModel.currentCharacter.collectAsState()

    LaunchedEffect(character) {
        character?.let {
            characterName = it.characterName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$characterName's Inventory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Serif,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(parchment)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("EQUIPMENT", color = deepBrown, fontWeight = FontWeight.Bold, fontFamily = Serif)

            if (items.isEmpty()) {
                Text("No equipment available.", fontFamily = Serif)
            } else {
                items.forEach { item ->
                    ItemCard(item = item, onClick = {
                        navController.navigate("item_Detail/${item.itemId}")
                    })
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, oak),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, fontFamily = Serif, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.description, fontFamily = Serif, fontSize = 14.sp, maxLines = 2)
        }
    }
}