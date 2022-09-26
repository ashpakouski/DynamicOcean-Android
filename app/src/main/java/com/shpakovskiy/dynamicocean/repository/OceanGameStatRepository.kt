package com.shpakovskiy.dynamicocean.repository

import android.content.Context
import android.content.SharedPreferences

class OceanGameStatRepository(context: Context) : GameStatRepository {
    companion object {
        private const val DYNAMIC_OCEAN = "dynamic_ocean"
        private const val BEST_ATTEMPT = "best_attempt"
        const val BEST_ATTEMPT_TIME_UNAVAILABLE = Long.MAX_VALUE
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(DYNAMIC_OCEAN, Context.MODE_PRIVATE)

    override var bestAttemptTime: Long
        get() = sharedPreferences.getLong(BEST_ATTEMPT, BEST_ATTEMPT_TIME_UNAVAILABLE)
        set(gameTime) {
            val editor = sharedPreferences.edit()
            editor.putLong(BEST_ATTEMPT, gameTime)
            editor.apply()
        }
}