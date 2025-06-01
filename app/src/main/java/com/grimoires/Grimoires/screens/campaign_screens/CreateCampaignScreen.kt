package com.grimoires.Grimoires.screens.campaign_screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.grimoires.Grimoires.ui.element_views.CustomDrawerContent
import com.grimoires.Grimoires.viewmodel.CampaignViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCampaignScreen(
    navController: NavHostController,
    campaignViewModel: CampaignViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF5F5F5)
            ) {
                CustomDrawerContent(
                    nickname = "Usuario",
                    onOptionSelected = { option ->
                        when (option) {
                            "MY CHARACTERS" -> navController.navigate("characters")
                            "MY CAMPAIGNS" -> navController.navigate("campaigns")
                            "THE LIBRARY" -> navController.navigate("library")
                            "DICE CALCULATOR" -> navController.navigate("calculator")
                            "profile" -> navController.navigate("userProfileSection")
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Grimoires",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                shadow = Shadow(blurRadius = 4f, color = Color.Black)
                            ),
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFB44B33),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },

            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8EFE4))
                        .padding(horizontal = 24.dp)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(16.dp))

                    LabeledTextField(
                        label = "NAME",
                        value = name,
                        onValueChange = { name = it },
                        cornerRadius = 12.dp,
                        backgroundColor = Color(0xFFEFEFEF)
                    )


                    LabeledTextField(
                        label = "GENRE",
                        value = genre,
                        onValueChange = { genre = it },
                        cornerRadius = 12.dp,
                        backgroundColor = Color(0xFFEFEFEF)
                    )

                    Text(
                        text = "DESCRIPTION:",
                        color = Color(0xFF8B3A2C),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Divider(
                        thickness = 1.dp,
                        color = Color.Black,
                    )
                    OutlinedTextField(
                        value = description,
                        shape = RoundedCornerShape(12.dp),
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFEFEF))
                            .height(200.dp),

                        )

                    Button(
                        onClick = { code = generateCode() },
                        enabled = code.isBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB44B33)),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("GENERATE CODE", color = Color.White)
                    }

                    Text(
                        text = "CODE:",
                        color = Color(0xFF8B3A2C),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFEFEF))
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (code.isEmpty()) "XXXXXX" else code,
                            modifier = Modifier.weight(1f),
                            color = Color.Black
                        )
                        IconButton(onClick = {
                            if (code.isNotEmpty()) {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Campaign Code", code)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.Black
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                            if (name.isBlank() || genre.isBlank() || description.isBlank() || code.isBlank()) {
                                Toast.makeText(context, "Please fill all fields and generate a code.", Toast.LENGTH_SHORT).show()
                            } else if (currentUserId.isNullOrBlank()) {
                                Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
                            } else {
                                campaignViewModel.createNewCampaign(
                                    title = name,
                                    description = description,
                                    masterId = currentUserId,
                                    code = code,
                                    onSuccess = {
                                        navController.popBackStack()
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D9B82)),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("SAVE CAMPAIGN", color = Color.White)
                    }
                }
            }
        )
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    cornerRadius: Dp = 12.dp,
    backgroundColor: Color = Color(0xFFEFEFEF)

) {
    Column {
        Text(
            text = "$label:",
            color = Color(0xFF8B3A2C),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            thickness = 1.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cornerRadius),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = backgroundColor
            )
        )

    }
}


fun generateCode(): String {
    val chars = ('A'..'Z') + ('0'..'9')
    return List(6) { chars.random() }.joinToString("")
}
