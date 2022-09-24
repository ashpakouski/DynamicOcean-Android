package com.shpakovskiy.dynamicocean.model

import android.graphics.RectF

class DisplayCutout(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width = right - left
    val height = bottom - top
}

fun RectF.toDisplayCutout(): DisplayCutout {
    return DisplayCutout(
        left = this.left,
        top = this.top,
        right = this.right,
        bottom = this.bottom
    )
}