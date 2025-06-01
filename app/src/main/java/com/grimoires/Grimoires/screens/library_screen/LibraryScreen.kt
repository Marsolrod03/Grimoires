package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.domain.model.LibraryItem
import com.grimoires.Grimoires.ui.element_views.CustomDrawerContent
import com.grimoires.Grimoires.ui.element_views.toLibraryItem
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavHostController,
    nickname: String,
    viewModel: CatalogViewModel
) {
    val races by viewModel.races.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val items by viewModel.items.collectAsState()
    val spells by viewModel.spells.collectAsState()

    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val libraryItems = when (selectedCategory) {
        "Race" -> races.map { it.toLibraryItem() }
        "Class" -> classes.map { it.toLibraryItem() }
        "Item" -> items.map { it.toLibraryItem() }
        "Spell" -> spells.map { it.toLibraryItem() }
        "All" -> {
            val allItems = mutableListOf<LibraryItem>()
            allItems.addAll(races.map { it.toLibraryItem() })
            allItems.addAll(classes.map { it.toLibraryItem() })
            allItems.addAll(items.map { it.toLibraryItem() })
            allItems.addAll(spells.map { it.toLibraryItem() })
            allItems
        }
        else -> {
            val allItems = mutableListOf<LibraryItem>()
            allItems.addAll(races.map { it.toLibraryItem() })
            allItems.addAll(classes.map { it.toLibraryItem() })
            allItems.addAll(items.map { it.toLibraryItem() })
            allItems.addAll(spells.map { it.toLibraryItem() })
            allItems
        }
    }.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawerContent(
                nickname = nickname,
                onOptionSelected = { option ->
                    when (option) {
                        "MY CHARACTERS" -> navController.navigate("characters")
                        "MY CAMPAIGNS" -> navController.navigate("campaigns")
                        "THE LIBRARY" -> {}
                        "DICE CALCULATOR" -> navController.navigate("calculator")
                        "profile" -> navController.navigate("userProfileSection")
                    }
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Grimoires", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFB44B33),
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7EDDF))
                    .padding(16.dp)
                    .padding(padding)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Filter", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF7C3A2D))
                    Spacer(Modifier.width(16.dp))
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.buttonColors(Color(0xFFB14B34)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(selectedCategory.uppercase())
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("All", "Race", "Class", "Item", "Spell").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        selectedCategory = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(libraryItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("detail/${item.type}/${item.id}")
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFB14B34))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = item.title.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}
