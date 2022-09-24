package com.shpakovskiy.dynamicocean.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.shpakovskiy.dynamicocean.model.DisplayCutout

class DeviceScreenDataRepository(context: Context) : ScreenDataRepository {
    companion object {
        private const val SCREEN_WIDTH = "screen_width"
        private const val SCREEN_HEIGHT = "screen_height"
        private const val DISPLAY_CUTOUT = "display_cutout"
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
        set(height) {
            val editor = sharedPreferences.edit()
            editor.putInt(SCREEN_HEIGHT, height)
            editor.apply()
        }

    override var displayCutout: DisplayCutout?
        get() {
            sharedPreferences.getString(DISPLAY_CUTOUT, null)?.let {
                return Gson().fromJson(it, DisplayCutout::class.java)
            }
            return null
        }
        set(cutout) {
            val editor = sharedPreferences.edit()
            editor.putString(DISPLAY_CUTOUT, Gson().toJson(cutout))
            editor.apply()
        }

}