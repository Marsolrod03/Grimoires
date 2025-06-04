package com.grimoires.Grimoires.screens.authentication_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.ui.models.LoginViewModel


@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel) {
    val backgroundColor = Color(0xFFF7E9D4)
    val primaryColor = Color(0xFFB44B33)
    val textFieldColor = Color(0xFFE29176)


    val email by viewModel.email
    val password by viewModel.password
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        Firebase.auth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "< Back",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp, top = 16.dp)
                .clickable { navController.navigate("welcome") },
            color = primaryColor,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "Destiny brings you\nback...",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontFamily = Serif,
            color = primaryColor,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.swords),
            contentDescription = "Swords",
            modifier = Modifier.height(60.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(primaryColor)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Email Address",
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email",
                color = textFieldColor
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Password",
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Password",
                color = textFieldColor,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            viewModel.errorMessage.value?.let { errorText ->
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            LoadingButton(
                isLoading = isLoading,
                onClick = { viewModel.login() },
                backgroundColor = backgroundColor,
                textColor = primaryColor,
                text = "SIGN IN"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Forgot Password?",
                color = backgroundColor,
                textDecoration = TextDecoration.Underline,
                fontSize = 14.sp,
                modifier = Modifier.clickable { navController.navigate("forgot") }
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    color: Color,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = color,
            unfocusedContainerColor = color,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White.copy(alpha = 0.7f),
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
    )
}

@Composable
fun LoadingButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    text: String
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}