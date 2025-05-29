package com.grimoires.Grimoires.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grimoires.Grimoires.domain.model.Dice
import com.grimoires.Grimoires.ui.element_views.DiceView
import com.grimoires.Grimoires.ui.element_views.RollHistoryItem
import com.grimoires.Grimoires.ui.element_views.RollResultItem
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceCalculatorScreen() {

    val dice = remember {
        mutableStateListOf(
            Dice(type = 4),  // D4
            Dice(type = 6),  // D6
            Dice(type = 8),  // D8
            Dice(type = 10), // D10
            Dice(type = 12), // D12
            Dice(type = 20), // D20
            Dice(type = 100) // D100
        )
    }

    val rollResults = remember { mutableStateListOf<Pair<Int, Int>>() }

    val rollHistory = remember { mutableStateListOf<List<Int>>() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    fun rollDie(dieIndex: Int) {
        val die = dice[dieIndex]
        die.rollCount++
        val result = Random.nextInt(1, die.type + 1)
        die.currentValue = result
        rollResults.add(Pair(dieIndex, result))
    }

    fun rollAllDice() {
        rollResults.clear()
        rollHistory.add(dice.map { it.currentValue })
        dice.forEachIndexed { index, _ ->
            rollDie(index)
        }
    }


    fun resetDice() {
        dice.forEach {
            it.rollCount = 0
            it.currentValue = 0
        }
        rollResults.clear()
        rollHistory.clear()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grimoires", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB44B33),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { rollAllDice() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB44B33),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("ROLL ALL")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { resetDice() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB44B33),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("RESET")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color(0xFFF7E9D4)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0..3) {
                    DiceView(
                        dice = dice[i],
                        onClick = { rollDie(i) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 4..6) {
                    DiceView(
                        dice = dice[i],
                        onClick = { rollDie(i) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ROLL RESULTS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (rollResults.isEmpty()) {
                Text(
                    text = "Click on dice to roll them",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rollResults) { (dieIndex, result) ->
                        RollResultItem(
                            dieType = dice[dieIndex].type,
                            result = result
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (rollHistory.isNotEmpty()) {
                Text(
                    text = "ROLL HISTORY",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    rollHistory.reversed().forEachIndexed { index, results ->
                        RollHistoryItem(
                            results = results,
                            dice = dice,
                            rollNumber = rollHistory.size - index
                        )
                    }
                }
            }
        }
    }
}
