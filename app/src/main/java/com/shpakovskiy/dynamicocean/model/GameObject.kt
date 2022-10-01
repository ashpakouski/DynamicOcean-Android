package com.shpakovskiy.dynamicocean.model

// Common rect structure
data class GameObject(
    val x: Float,
    val y: Float,
    val width: Int,
    val height: Int,
    val isMirrored: Boolean
)