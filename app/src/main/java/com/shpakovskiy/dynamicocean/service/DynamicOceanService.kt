package com.shpakovskiy.dynamicocean.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shpakovskiy.dynamicocean.R
import com.shpakovskiy.dynamicocean.view.DynamicOcean

class DynamicOceanService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.shpakovskiy.dynamicocean"
        private const val NOTIFICATION_CHANNEL_NAME = "Dynamic ocean service"
    }

    override fun onCreate() {
        super.onCreate()

        startForegroundService()

        DynamicOcean(this).create()
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

        startForeground(2, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}