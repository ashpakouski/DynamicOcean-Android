package com.shpakovskiy.dynamicocean.repository

import android.content.Context
import com.shpakovskiy.dynamicocean.common.Constants

class OceanGameSettingsRepository(context: Context) :
    SharedPreferencesRepository(context),
    GameSettingsRepository {

    companion object {
        private const val DIFFICULTY = "difficulty"
        private const val DIFFICULTY_DEFAULT = Constants.Difficulty.DEFAULT
    }

    override var difficulty: Int
        get() = sharedPreferences.getInt(DIFFICULTY, DIFFICULTY_DEFAULT)
        set(difficulty) {
            val editor = sharedPreferences.edit()
            editor.putInt(DIFFICULTY, difficulty)
            editor.apply()
        }
}