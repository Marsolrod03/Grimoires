package com.grimoires.Grimoires.screens.library_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoires.Grimoires.domain.model.Item

@Composable
fun ItemDetailScreen(item: Item, onBack: () -> Unit) {
    DetailScaffold(title = item.name, onBack = onBack) {
        Column {
            Text("Type:", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = FontFamily.Serif)
            Text(item.type)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Description:", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = FontFamily.Serif)
            Text(item.description)
        }
    }
}
