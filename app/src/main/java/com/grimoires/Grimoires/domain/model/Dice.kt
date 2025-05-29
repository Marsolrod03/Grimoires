package com.grimoires.Grimoires.domain.model

data class Dice(
    val type: Int,
    var rollCount: Int = 0,
    var currentValue: Int = 0
)