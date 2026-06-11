package com.shpak.dynamicocean.core.model

data class RectPx(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int
        get() = right - left

    val height: Int
        get() = bottom - top

    val center: PointPx
        get() = PointPx(
            x = left + width / 2f,
            y = top + height / 2f
        )
}