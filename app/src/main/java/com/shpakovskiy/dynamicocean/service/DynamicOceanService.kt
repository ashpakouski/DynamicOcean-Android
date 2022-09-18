package com.shpakovskiy.dynamicocean.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shpakovskiy.dynamicocean.R
import com.shpakovskiy.dynamicocean.view.DynamicOcean

class DynamicOceanService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.shpakovskiy.dynamicocean"
        private const val NOTIFICATION_CHANNEL_NAME = "Dynamic ocean service"
    }

    private var mSensorManager: SensorManager? = null
    private var mLight: Sensor? = null
    private var dynamicOcean: DynamicOcean? = null

    private val mLightSensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // [0] | Left -> [0; 10] | Right -> [0; -10]
            // [0] | Down -> [0; -10] | Up -> [0; 10]
            dynamicOcean?.moveTo(-1 * event.values[0], event.values[1])
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
    }

    override fun onCreate() {
        super.onCreate()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLight = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mLight?.let {
            mSensorManager?.registerListener(
                mLightSensorListener, mLight,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        startForegroundService()

        dynamicOcean = DynamicOcean(this)
        dynamicOcean?.create()
    }

    private fun startForegroundService() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_MIN
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val notification =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setOngoing(true)
                .setContentTitle("Dynamic ocean is running")
                .setContentText("Displaying over other apps")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()

        startForeground(42, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}