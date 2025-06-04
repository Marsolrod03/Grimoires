package com.grimoires.Grimoires.screens.catalog_screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.domain.model.Item
import com.grimoires.Grimoires.ui.models.CatalogViewModel
import com.grimoires.Grimoires.ui.models.PlayableCharacterViewModel
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.leafGreen
import com.grimoires.Grimoires.ui.theme.lightTan
import com.grimoires.Grimoires.ui.theme.oak
import com.grimoires.Grimoires.ui.theme.parchment
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentSelectionScreen(
    navController: NavHostController,
    characterId: String,
    viewModel: CatalogViewModel = viewModel(LocalContext.current as ViewModelStoreOwner),
    characterViewModel: PlayableCharacterViewModel = viewModel()
) {
    val allItems by viewModel.items.collectAsState()
    val selectedItems = remember { mutableStateListOf<String>() }
    var selectedType by remember { mutableStateOf("All") }

    val itemTypes = remember(allItems) {
        listOf("All") + allItems.map { it.type }.distinct().sorted()
    }

    val filteredItems = remember(allItems, selectedType) {
        if (selectedType == "All") allItems else allItems.filter { it.type == selectedType }
    }

    LaunchedEffect(characterId) {
        characterViewModel.loadItemsByIds(characterId) { equippedItems ->
            selectedItems.clear()
            selectedItems.addAll(equippedItems.map { it.itemId })
        }
        if (allItems.isEmpty()) {
            viewModel.fetchItems()
        }
    }

    Scaffold(
        containerColor = parchment,
        topBar = {
            TopAppBar(
                title = { Text("Equipment", fontSize = 28.sp, fontFamily = FontFamily.Serif, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepBrown)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    characterViewModel.saveSelectedItems(characterId, selectedItems)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = leafGreen)
            ) {
                Text("SAVE EQUIPMENT", color = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ITEM TYPE:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor().width(180.dp),
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        itemTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredItems.isEmpty()) {
                Text("No items available.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        items = filteredItems,
                        key = { it.itemId }
                    ) { item ->
                        EquipmentItemCard(
                            item = item,
                            isSelected = selectedItems.contains(item.itemId),
                            onSelectionChange = { selected ->
                                if (selected) {
                                    if (!selectedItems.contains(item.itemId)) {
                                        selectedItems.add(item.itemId)
                                    }
                                } else {
                                    selectedItems.remove(item.itemId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentItemCard(
    item: Item,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChange(!isSelected) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = parchment),
        border = BorderStroke(2.dp, if (isSelected) leafGreen else deepBrown)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(item.name, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                item.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(it, maxLines = 2, fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}