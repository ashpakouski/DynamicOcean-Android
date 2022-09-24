package com.shpakovskiy.dynamicocean

import android.content.Intent
import android.graphics.RectF
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shpakovskiy.dynamicocean.model.toDisplayCutout
import com.shpakovskiy.dynamicocean.repository.DeviceScreenDataRepository
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import com.shpakovskiy.dynamicocean.service.DynamicOceanService

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var screenDataRepository: ScreenDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screenDataRepository = DeviceScreenDataRepository(applicationContext)

        checkOverlayPermission()
    }

    override fun onResume() {
        super.onResume()

        startService()
    }

    private fun startService() {
        if (Settings.canDrawOverlays(this)) {
            startForegroundService(Intent(this, DynamicOceanService::class.java))
        }
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    // TODO: Handle cases, when device doesn't have any cutouts
    override fun onAttachedToWindow() {
        screenDataRepository.screenWidth = window.windowManager.maximumWindowMetrics.bounds.right
        screenDataRepository.screenHeight = window.windowManager.maximumWindowMetrics.bounds.bottom

        val displayCutout = window.decorView.rootWindowInsets.displayCutout
        if (displayCutout != null) {
            val cutoutBounds = RectF()
            val cutoutPath = displayCutout.cutoutPath

            if (cutoutPath != null) {
                cutoutPath.computeBounds(cutoutBounds, true)
                screenDataRepository.displayCutout = cutoutBounds.toDisplayCutout()
                Log.d(TAG, "Cutout: ${screenDataRepository.displayCutout}")
            } else {
                Log.e(TAG, "There is no display cutout path")
            }
        } else {
            Log.e(TAG, "There is no display cutout")
        }
    }
}