package com.shpakovskiy.dynamicocean.repository

import android.content.Context
import android.content.SharedPreferences

class DeviceScreenDataRepository(context: Context) : ScreenDataRepository {
    companion object {
        private const val SCREEN_WIDTH = "screen_width"
        private const val SCREEN_HEIGHT = "screen_height"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("dynamic_ocean", Context.MODE_PRIVATE)

    override var screenWidth: Int
        get() = sharedPreferences.getInt(SCREEN_WIDTH, -1)
        set(width) {
            val editor = sharedPreferences.edit()
            editor.putInt(SCREEN_WIDTH, width)
            editor.apply()
        }

    override var screenHeight: Int
        get() = sharedPreferences.getInt(SCREEN_HEIGHT, -1)
        set(width) {
            val editor = sharedPreferences.edit()
            editor.putInt(SCREEN_HEIGHT, width)
            editor.apply()
        }
}