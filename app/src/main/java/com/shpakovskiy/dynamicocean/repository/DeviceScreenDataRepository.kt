package com.shpakovskiy.dynamicocean.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.shpakovskiy.dynamicocean.model.DeviceScreen
import com.shpakovskiy.dynamicocean.model.DisplayCutout

class DeviceScreenDataRepository(context: Context) :
    SharedPreferencesRepository(context),
    ScreenDataRepository {

    companion object {
        private const val DEVICE_SCREEN = "device_screen"
        private const val DISPLAY_CUTOUT = "display_cutout"
    }

    override var deviceScreen: DeviceScreen?
        get() {
            sharedPreferences.getString(DEVICE_SCREEN, null)?.let {
                return Gson().fromJson(it, DeviceScreen::class.java)
            }
            return null
        }
        set(screen) {
            val editor = sharedPreferences.edit()
            editor.putString(DEVICE_SCREEN, Gson().toJson(screen))
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