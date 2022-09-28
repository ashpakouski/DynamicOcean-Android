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
import com.shpakovskiy.dynamicocean.controller.ControllerEventObserver
import com.shpakovskiy.dynamicocean.controller.GameController
import com.shpakovskiy.dynamicocean.controller.DynamicOceanController
import com.shpakovskiy.dynamicocean.repository.DeviceScreenDataRepository
import com.shpakovskiy.dynamicocean.repository.OceanGameStatRepository
import com.shpakovskiy.dynamicocean.view.DynamicOcean
import kotlin.math.abs
import kotlin.math.sqrt

class DynamicOceanService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.shpakovskiy.dynamicfield"
        private const val NOTIFICATION_CHANNEL_NAME = "Dynamic ocean service"

        private const val SERIAL_SHAKES_DELAY_MILLIS = 1000L
        private const val SHAKE_ACCELERATION = 10L

        // Well, I didn't want it, but I'm gonna give it a chance for now
        var isRunning = false
    }

    // Game properties
    private lateinit var gameController: GameController
    private var isGameFieldCreated = false

    // Acceleration detector properties
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var acceleration = 0F
    private var currentAcceleration = 0F
    private var lastAcceleration = 0F
    private var lastShakeTime = 0L

    private val accelerometerListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            // [0] | Left -> [0; 10] | Right -> [0; -10]
            // [0] | Down -> [0; -10] | Up -> [0; 10]

            // Get acceleration by each axis
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]

            if (!isGameFieldCreated) {
                // Save previous acceleration and get new resulting acceleration as sum of vectors
                lastAcceleration = currentAcceleration
                currentAcceleration = sqrt(x * x + y * y + z * z)

                // Calculate difference and "smoothify" acceleration
                val delta = currentAcceleration - lastAcceleration
                acceleration = acceleration * 0.9F + delta

                val now = System.currentTimeMillis()
                if (now - lastShakeTime > SERIAL_SHAKES_DELAY_MILLIS &&
                    currentAcceleration != 0F && lastAcceleration != 0F &&
                    abs(acceleration) > SHAKE_ACCELERATION
                ) {
                    gameController.createGameField()
                    gameController.startGame()

                    isGameFieldCreated = true

                    lastShakeTime = now
                }
            } else {
                gameController.moveObject(-1 * x, y)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
    }

    private val controllerLifecycleObserver = object : ControllerEventObserver {
        override fun onGameFinished() {
            isGameFieldCreated = false
        }
    }

    override fun onCreate() {
        super.onCreate()

        isRunning = true

        startForegroundService()
        registerAccelerometerListener()

        gameController = DynamicOceanController(
            gameListener = DynamicOcean(applicationContext),
            screenDataRepository = DeviceScreenDataRepository(applicationContext),
            gameStatRepository = OceanGameStatRepository(applicationContext)
        )

        gameController.setEventObserver(controllerLifecycleObserver)
    }

    private fun registerAccelerometerListener() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.let {
            sensorManager?.registerListener(
                accelerometerListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun unregisterAccelerometerListener() {
        sensorManager?.unregisterListener(accelerometerListener)
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

    override fun onDestroy() {
        super.onDestroy()

        unregisterAccelerometerListener()
        isRunning = false
    }
}