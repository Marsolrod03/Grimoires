package com.grimoires.Grimoires.screens.catalog_screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.grimoires.Grimoires.domain.model.Spell
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.parchment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellsScreen(
    characterId: String,
    viewModel: PlayableCharacterViewModel,
    navController: NavController,
    onBackClick: () -> Unit
) {
    LaunchedEffect(characterId) {
        viewModel.loadCharacterById(characterId)
    }

    val character by viewModel.currentCharacter.collectAsState()
    var spells by remember { mutableStateOf<List<Spell>>(emptyList()) }


    LaunchedEffect(characterId) {
        viewModel.loadSpellsByIds(characterId) { loadedSpells ->
            spells = loadedSpells
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${character?.characterName ?: "Character"}'s Spells",
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
            Text("SPELLS", color = deepBrown, fontWeight = FontWeight.Bold, fontFamily = Serif)

            if (spells.isEmpty()) {
                Text("No spells known.", fontFamily = Serif)
            } else {
                spells.forEach { spell ->
                    SpellDisplay(spell = spell) {
                        navController.navigate("spell_detail/${spell.spellId}")
                    }
                }
            }
        }
    }
}


    @Composable
    fun SpellDisplay(
        spell: Spell,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = parchment),
            border = BorderStroke(2.dp, deepBrown),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = spell.name,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Level ${spell.level}",
                        color = deepBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = spell.description,
                    maxLines = 2,
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp
                )
            }
        }
    }