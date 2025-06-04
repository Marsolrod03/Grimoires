package com.grimoires.Grimoires.ui.models

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.grimoires.Grimoires.domain.model.Note
import com.grimoires.Grimoires.ui.theme.deepBrown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes
    private val firestore = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    fun loadNotes(campaignId: String) {
        listener?.remove()

        listener = firestore.collection("notes")
            .whereEqualTo("campaignID", campaignId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotesViewModel", "Error listening to notes: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _notes.value = snapshot.documents.mapNotNull { doc ->
                        try {
                            Note(
                                idNote = doc.id,
                                campaignID = campaignId,
                                content = doc.getString("content") ?: "",
                                authorID = doc.getString("authorId") ?: "",
                                authorName = doc.getString("authorName") ?: "Autor desconocido",
                                date = doc.getTimestamp("timestamp") ?: Timestamp.now()
                            )
                        } catch (e: Exception) {
                            Log.e("NotesViewModel", "Error mapping note ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    Log.d("NotesViewModel", "Loaded ${_notes.value.size} notes")
                }
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }



    fun addNote(campaignId: String, content: String, authorId: String) {
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(authorId).get().await()
                val authorName = userDoc.getString("name") ?: "Usuario desconocido"

                val note = hashMapOf(
                    "campaignID" to campaignId,
                    "content" to content,
                    "authorId" to authorId,
                    "authorName" to authorName,
                    "timestamp" to Timestamp.now()
                )

                firestore.collection("notes")
                    .add(note)
                    .await()

                Log.d("NotesViewModel", "Note added successfully")
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Error adding note: ${e.message}")
            }
        }
    }



    fun deleteNote(campaignId: String, noteId: String) {
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("notes")
                    .document(noteId)
                    .delete()
                    .await()
                loadNotes(campaignId)
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Error deleting note: ${e.message}")
            }
        }
    }
}


@Composable
fun PublicNotesSection(
    campaignId: String,
    userId: String,
    isMaster: Boolean,
    notesViewModel: NotesViewModel
) {
    val notes by notesViewModel.notes.collectAsState()
    var newNote by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "NOTES",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            color = deepBrown,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No notes yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(notes.reversed()) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    if (isMaster) {
                                        notesViewModel.deleteNote(campaignId, note.idNote)
                                    }
                                }
                            ),
                        border = BorderStroke(1.dp, deepBrown)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text(note.content)
                            Text(
                                text = "Por ${note.authorName} - ${
                                    note.date?.toDate()?.toString()?.substring(0, 16) ?: ""
                                }",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newNote,
                onValueChange = { newNote = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Write a note...") }
            )
            IconButton(
                onClick = {
                    if (newNote.isNotBlank()) {
                        notesViewModel.addNote(campaignId, newNote, userId)
                        newNote = ""
                    }
                },
                enabled = newNote.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}