package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.data.classes.MenuAd


class HomeScreenViewModel
    : ViewModel() {

    private val _ads = mutableStateOf(
        listOf(
            MenuAd(
                "How to start a campaign",
                "As for starting the campaign itself... Well first...",
                R.drawable.campaign
            ),
            MenuAd(
                "Tips to create a character",
                "For your first character, put a little bit of yourself into it...",
                R.drawable.character
            ),
            MenuAd(
                "Best spells for wizards?",
                "Other spells include shield, misty step, counterspell...",
                R.drawable.spells
            )
        )
    )
    val ads: State<List<MenuAd>> = _ads

}



