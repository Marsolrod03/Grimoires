package com.grimoires.Grimoires.screens.note_screens
/*

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grimoires.Grimoires.domain.model.Note
import com.grimoires.Grimoires.ui.models.UserViewModel
import com.grimoires.Grimoires.viewmodel.CampaignViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController,
    campaignId: String,
    campaignViewModel: CampaignViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var noteText by remember { mutableStateOf("") }

    val notes by campaignViewModel.getNotes(campaignId).collectAsState(emptyList())

    val authorId = userViewModel.uid
    val authorName = userViewModel.nickname

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas de Campaña") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (noteText.isNotBlank()) {
                        if (authorId != null) {
                            campaignViewModel.addNote(
                                campaignId = campaignId,
                                text = noteText,
                                authorId = authorId,
                                authorName = authorName,
                                onSuccess = {
                                    Toast.makeText(context, "Nota enviada", Toast.LENGTH_SHORT).show()
                                    campaignViewModel.sendNotificationToCampaign(
                                        title = "Nueva nota en la campaña",
                                        message = noteText,
                                        senderId = authorId
                                    )
                                    noteText = ""
                                },
                                onError = {
                                    Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Enviar nota")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    if (noteText.isEmpty()) {
                        Text("Escribe una nota...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Notas:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (notes.isEmpty()) {
                Text("No hay notas aún...")
            } else {
                notes.forEach { note ->
                    NoteItem(note)
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.content)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Por ${note.authorName} - ${note.date?.toDate()?.toString() ?: "Sin fecha"}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


 */