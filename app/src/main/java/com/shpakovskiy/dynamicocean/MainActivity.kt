package com.shpakovskiy.dynamicocean

import android.Manifest
import android.content.Intent
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.shpakovskiy.dynamicocean.model.DeviceScreen
import com.shpakovskiy.dynamicocean.model.DisplayCutout
import com.shpakovskiy.dynamicocean.model.toDisplayCutout
import com.shpakovskiy.dynamicocean.repository.DeviceScreenDataRepository
import com.shpakovskiy.dynamicocean.repository.OceanGameStatRepository
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import com.shpakovskiy.dynamicocean.service.DynamicOceanService

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    // Repositories
    private lateinit var screenDataRepository: ScreenDataRepository
    private lateinit var gameStatRepository: OceanGameStatRepository

    // Views
    private lateinit var bestTimeView: TextView
    private lateinit var overlaySwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var oceanLauncherButton: MaterialButton
    private lateinit var warningView: TextView

    // Permissions
    private var isOverlayPermissionGranted = false
    private var isNotificationsPermissionGranted = false

    private fun areRequiredPermissionsGranted(): Boolean =
        isOverlayPermissionGranted && isNotificationsPermissionGranted

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        displayPermissionsStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Repositories
        screenDataRepository = DeviceScreenDataRepository(applicationContext)
        gameStatRepository = OceanGameStatRepository(applicationContext)

        // Views
        bestTimeView = findViewById(R.id.best_time_view)
        overlaySwitch = findViewById(R.id.switch_overlay_permission)
        notificationsSwitch = findViewById(R.id.switch_notifications_permission)
        oceanLauncherButton = findViewById(R.id.ocean_launcher_button)
        warningView = findViewById(R.id.warning_view)

        // View callbacks
        overlaySwitch.setOnClickListener {
            openOverlaySettings()
        }

        notificationsSwitch.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        oceanLauncherButton.setOnClickListener {
            if (!DynamicOceanService.isRunning) {
                startService()
                updateActivationButton(true)
            } else {
                stopService()
                updateActivationButton(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Update best time view
        val bestTime = gameStatRepository.bestAttemptTime
        if (bestTime != OceanGameStatRepository.BEST_ATTEMPT_TIME_UNAVAILABLE) {
            val bestTimeText = "${(bestTime / 1000.0F)}s"
            bestTimeView.text = bestTimeText
        }

        // Permissions check
        isOverlayPermissionGranted = Settings.canDrawOverlays(this)
        isNotificationsPermissionGranted =
            NotificationManagerCompat.from(this).areNotificationsEnabled()

        // Update UI according to permissions status
        displayPermissionsStatus()
        updateActivationButton()
    }

    private fun startService() {
        if (Settings.canDrawOverlays(this)) {
            startForegroundService(Intent(this, DynamicOceanService::class.java))
        }
    }

    private fun stopService() {
        stopService(Intent(this, DynamicOceanService::class.java))
    }

    // TODO: Handle cases, when device doesn't have any cutouts
    // TODO: Decompose method logic
    override fun onAttachedToWindow() {
        screenDataRepository.deviceScreen = DeviceScreen(
            width = window.windowManager.maximumWindowMetrics.bounds.right,
            height = window.windowManager.maximumWindowMetrics.bounds.bottom,
            statusBarHeight = window.decorView.rootWindowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.systemBars()
            ).top
        )

        val displayCutout = window.decorView.rootWindowInsets.displayCutout

        if (displayCutout != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val cutoutBounds = RectF()
                val cutoutPath = displayCutout.cutoutPath

                if (cutoutPath != null) {
                    cutoutPath.computeBounds(cutoutBounds, true)
                    screenDataRepository.displayCutout = cutoutBounds.toDisplayCutout()
                } else {
                    Log.e(TAG, "There is no display cutout path")
                }
            } else {
                val rect = displayCutout.boundingRects.first()
                screenDataRepository.displayCutout = DisplayCutout(
                    left = rect.left.toFloat(),
                    top = rect.top.toFloat(),
                    right = rect.right.toFloat(),
                    bottom = rect.bottom.toFloat()
                )
            }
        } else {
            Log.e(TAG, "There is no display cutout")
        }
    }

    private fun openOverlaySettings() {
        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
    }

    private fun displayPermissionsStatus() {
        overlaySwitch.isChecked = isOverlayPermissionGranted
        overlaySwitch.isClickable = !isOverlayPermissionGranted

        notificationsSwitch.isChecked = isNotificationsPermissionGranted
        notificationsSwitch.isClickable = !isNotificationsPermissionGranted

        oceanLauncherButton.isEnabled = areRequiredPermissionsGranted()
        warningView.visibility = if (areRequiredPermissionsGranted()) View.GONE else View.VISIBLE
    }

    // `isServiceRunning` parameter has to be added, as service doesn't change
    // corresponding value quick enough bu itself
    private fun updateActivationButton(isServiceRunning: Boolean = DynamicOceanService.isRunning) {
        oceanLauncherButton.text = if (isServiceRunning) {
            "Tap to stop dynamic ocean"
        } else {
            "Tap to start dynamic ocean"
        }
    }
}