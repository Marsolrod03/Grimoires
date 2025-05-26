package com.grimoires.Grimoires.ui.element_views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun UserProfileSection(
    userName: String,
    userEmail: String,
    onNameChange: (String) -> Unit,
    accentColor: Color
) {
    var editingName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(userName) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (editingName) {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nuevo nombre") },
                    trailingIcon = {
                        IconButton(onClick = {
                            onNameChange(newName)
                            editingName = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar")
                        }
                    },
                    singleLine = true
                )
            } else {
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { editingName = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar nombre")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userEmail,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )
        }
    }
}