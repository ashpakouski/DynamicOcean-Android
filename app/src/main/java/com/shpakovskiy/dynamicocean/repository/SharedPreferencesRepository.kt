package com.shpakovskiy.dynamicocean.repository

import android.content.Context
import android.content.SharedPreferences

abstract class SharedPreferencesRepository(context: Context) {
    companion object {
        private const val DYNAMIC_OCEAN = "dynamic_ocean"
    }

    protected val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(DYNAMIC_OCEAN, Context.MODE_PRIVATE)
}