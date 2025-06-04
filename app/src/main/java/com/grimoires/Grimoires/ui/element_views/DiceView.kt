package com.grimoires.Grimoires.ui.element_views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoires.Grimoires.domain.model.DiceDTO
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.grimoires.Grimoires.R

@Composable
fun DiceView(dice: DiceDTO, onClick: () -> Unit) {
    val imageRes = when (dice.type) {
        4 -> R.drawable.d4
        6 -> R.drawable.d6
        8 -> R.drawable.d8
        10 -> R.drawable.d10
        12 -> R.drawable.d12
        20 -> R.drawable.d20
        else -> R.drawable.d100
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Dice image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (dice.rollCount > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Red, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dice.rollCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


    }
}

@Composable
fun RollResultItem(diceType: Int, result: Int) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(60.dp),
        colors = CardDefaults.cardColors(
            Color(0xFFB44B33)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "d$diceType",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = result.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF7E9D4)
            )
        }
    }
}


