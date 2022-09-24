package com.shpakovskiy.dynamicocean

import android.content.Intent
import android.graphics.RectF
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.DisplayCutout
import androidx.appcompat.app.AppCompatActivity
import com.shpakovskiy.dynamicocean.repository.DeviceScreenDataRepository
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import com.shpakovskiy.dynamicocean.service.DynamicOceanService

class MainActivity : AppCompatActivity() {
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

    override fun onAttachedToWindow() {
        screenDataRepository.screenWidth = window.windowManager.maximumWindowMetrics.bounds.right
        screenDataRepository.screenHeight = window.windowManager.maximumWindowMetrics.bounds.bottom

        Log.d("TAG123", "[${screenDataRepository.screenHeight}; ${screenDataRepository.screenWidth}]")

        val dc: DisplayCutout? = window.decorView.rootWindowInsets.displayCutout
        val rf = RectF()
        dc!!.cutoutPath!!.computeBounds(rf, true)

        Log.d("TAG123", "" + rf + "; " + window.windowManager.maximumWindowMetrics.bounds)
    }
}