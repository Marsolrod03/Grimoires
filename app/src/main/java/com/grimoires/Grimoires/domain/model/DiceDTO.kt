package com.grimoires.Grimoires.domain.model

data class DiceDTO(
    val type: Int,
    var rollCount: Int = 0,
    var currentValue: Int = 0
)