package com.grimoires.Grimoires.ui.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.grimoires.Grimoires.R
import com.grimoires.Grimoires.domain.model.MenuAd


class HomeScreenViewModel
    : ViewModel() {

    private val _ads = mutableStateOf(
        listOf(
            MenuAd(
                "How to start a campaign",
                "As for starting the campaign itself... Well first...",
                R.drawable.campaign,
                "https://www.reddit.com/r/DnD/comments/q2w5zp/how_tf_do_i_start_a_campaign/"
            ),
            MenuAd(
                "Tips to create a character",
                "For your first character, put a little bit of yourself into it...",
                R.drawable.character,
                "https://www.reddit.com/r/DnD/comments/63r917/character_creation_tips/"
            ),
            MenuAd(
                "Best spells for wizards?",
                "Other spells include shield, misty step, counterspell...",
                R.drawable.spells,
                "https://www.reddit.com/r/dndnext/comments/a56u1h/what_are_the_must_have_spells_for_a_wizard/"
            )
        )
    )
    val ads: State<List<MenuAd>> = _ads

}



