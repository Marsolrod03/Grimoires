package com.grimoires.Grimoires.ui.element_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoires.Grimoires.R


@Composable
fun CustomDrawerContent(
    nickname: String = "NICKNAME",
    onOptionSelected: (String) -> Unit
) {
    val backgroundColor = Color(0xFF7C3A2D)
    val textColor = Color.White
    val options = listOf(
        "MY CHARACTERS",
        "MY CAMPAIGNS",
        "THE LIBRARY",
        "DICE CALCULATOR"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))


        Image(
            painter = painterResource(id = R.drawable.icono1),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = nickname,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onOptionSelected("profile") }
                .padding(8.dp)
        )


        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            Divider(color = Color.Black.copy(alpha = 0.2f))
            Text(
                text = option,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }

        Divider(color = Color.Black.copy(alpha = 0.2f))
    }
}
