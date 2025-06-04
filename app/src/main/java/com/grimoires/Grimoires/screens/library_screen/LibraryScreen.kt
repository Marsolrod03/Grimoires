package com.grimoires.Grimoires.screens.library_screen

import HandleMenu
import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.domain.model.LibraryItem
import com.grimoires.Grimoires.ui.element_views.LibraryItemCard
import com.grimoires.Grimoires.ui.element_views.toLibraryItem
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.lightTan
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavHostController,
    nickname: StateFlow<String?>,
    viewModel: CatalogViewModel
) {
    val races by viewModel.races.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val items by viewModel.items.collectAsState()
    val spells by viewModel.spells.collectAsState()

    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

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

    libraryItems.forEach {
        println("LibraryItem -> id: '${it.id}', title: '${it.title}', type: '${it.type}'")
    }

    HandleMenu(nickname, navController) { scope, drawerState ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "THE LIBRARY",
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = deepBrown,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightTan)
                    .padding(16.dp)
                    .padding(padding)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Filter:",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        color = deepBrown
                    )
                    Spacer(Modifier.width(16.dp))
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.buttonColors(deepBrown),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(selectedCategory.uppercase())
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("All", "Race", "Class", "Item", "Spell").forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            type.replaceFirstChar { it.uppercase() },
                                            fontFamily = FontFamily.Serif
                                        )
                                    },
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


                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search...", fontFamily = FontFamily.Serif) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(libraryItems) { item ->
                        LibraryItemCard(item = item) {
                            val type = item.type.lowercase()
                            val id = item.id

                            println("Navegando a detalle: tipo=$type, id=$id")

                            if (id.isNotEmpty()) {
                                when (type) {
                                    "race" -> navController.navigate("detail/race/$id")
                                    "class" -> navController.navigate("detail/class/$id")
                                    "item" -> navController.navigate("detail/item/$id")
                                    "spell" -> navController.navigate("detail/spell/$id")

                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
