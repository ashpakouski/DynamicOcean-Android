package com.shpakovskiy.dynamicocean

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.shpakovskiy.dynamicocean.service.DynamicOceanService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
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
}