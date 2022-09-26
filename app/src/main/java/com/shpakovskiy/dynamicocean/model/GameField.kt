package com.shpakovskiy.dynamicocean.model

// Common rect structure
data class GameField(
    val x: Float,
    val y: Float,
    val heightCollapsed: Int,
    val widthCollapsed: Int,
    val heightExpanded: Int,
    val widthExpanded: Int
)