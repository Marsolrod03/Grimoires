package com.grimoires.Grimoires.screens.authentication_screens
import com.grimoires.Grimoires.ui.models.HandleState
import com.grimoires.Grimoires.ui.models.SignUpViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.grimoires.Grimoires.R


@Composable
fun SignUpScreen(navController: NavHostController) {
    val viewModel: SignUpViewModel = viewModel()

    val username = viewModel.username
    val nickname = viewModel.nickname
    val email = viewModel.email
    val password = viewModel.password
    val acceptedTerms = viewModel.acceptedTerms
    val currentState = viewModel.state

    LaunchedEffect(currentState) {
        if (currentState is HandleState.Success) {
            navController.navigate("home") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    val backgroundColor = Color(0xFFF7E9D4)
    val accentColor = Color(0xFFB44B33)
    val textFieldColor = Color(0xFFE29176)
    val primaryTextColor = Color.White


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "< Back",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp, top = 16.dp)
                .clickable { navController.navigate("welcome") },
            color = accentColor,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "The quest beckons...",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.swords),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(accentColor)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listOf(
                "Username" to username,
                "NickName" to nickname,
                "Email Address" to email,
                "Password" to password
            ).forEachIndexed { index, pair ->
                TextField(
                    value = pair.second,
                    onValueChange = {
                        when (index) {
                            0 -> viewModel.updateUsername(it)
                            1 -> viewModel.updateNickname(it)
                            2 -> viewModel.updateEmail(it)
                            3 -> viewModel.updatePassword(it)
                        }
                    },
                    isError = currentState is HandleState.Error,
                    placeholder = { Text(pair.first, color = Color.White.copy(alpha = 0.7f)) },
                    visualTransformation = if (pair.first == "Password") PasswordVisualTransformation() else VisualTransformation.None,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldColor,
                        unfocusedContainerColor = textFieldColor,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = primaryTextColor.copy(alpha = 0.7f),
                        unfocusedPlaceholderColor = primaryTextColor.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(50))
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = viewModel::updateAcceptedTerms,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White,
                        checkmarkColor = backgroundColor
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I agree to the terms and conditions",
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    textDecoration = TextDecoration.Underline
                )
            }

            Button(
                onClick = { viewModel.signUp() },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor
                ),
                enabled = currentState != HandleState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                if (currentState == HandleState.Loading) {
                    CircularProgressIndicator(
                        color = accentColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("SIGN UP", color = accentColor, fontWeight = FontWeight.Bold)
                }
            }

            if (currentState is HandleState.Error) {
                Text(
                    text = (currentState as HandleState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    if (currentState is HandleState.Error) {
        ErrorDialog(
            message = (currentState as HandleState.Error).message,
            onDismiss = { viewModel.state = HandleState.Idle }
        )
    }
}

@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error", color = Color.Black) },
        text = { Text(message, color = Color.Black) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB44B33))
            ) {
                Text("OK", color = Color.White)
            }
        }
    )
}