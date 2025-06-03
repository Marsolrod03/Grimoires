package com.grimoires.Grimoires.ui.element_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grimoires.Grimoires.domain.model.PlayableCharacter
import com.grimoires.Grimoires.ui.theme.deepBrown
import com.grimoires.Grimoires.ui.theme.parchment


@Composable
fun CharacterCard(character: PlayableCharacter, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = parchment),
        border = BorderStroke(2.dp, deepBrown),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("NAME: ${character.name}", color = deepBrown,fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                Spacer(modifier = Modifier.height(4.dp))
                Text("RACE: ${character.race}", color = deepBrown,fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                Spacer(modifier = Modifier.height(4.dp))
                Text("CLASS: ${character.characterClass}", color = deepBrown,fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            }
            Text("LEVEL: ${character.level}", color = deepBrown,fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        }
    }
}
