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
import com.shpakovskiy.dynamicocean.controller.ControllerLifecycleObserver
import com.shpakovskiy.dynamicocean.controller.GameController
import com.shpakovskiy.dynamicocean.controller.DynamicOceanController
import com.shpakovskiy.dynamicocean.repository.DeviceScreenDataRepository
import com.shpakovskiy.dynamicocean.view.DynamicOcean

class DynamicOceanService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.shpakovskiy.dynamicfield"
        private const val NOTIFICATION_CHANNEL_NAME = "Dynamic ocean service"
        // private const val STABILIZATION_CYCLES = 10

        // Well, I didn't want it, but I'm gonna give it a chance for now
        var isRunning = false
    }

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private lateinit var gameController: GameController

    // private var acceleration = 0f
    // private var currentAcceleration = 0f
    // private var lastAcceleration = 0f
    // private var serialShakes = 0

    private val accelerometerListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // [0] | Left -> [0; 10] | Right -> [0; -10]
            // [0] | Down -> [0; -10] | Up -> [0; 10]
            // Fetching x,y,z values
//            val x = event.values[0]
//            val y = event.values[1]
//            val z = event.values[2]
//            lastAcceleration = currentAcceleration
//
//            // Getting current accelerations
//            // with the help of fetched x,y,z values
//            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
//            val delta: Float = currentAcceleration - lastAcceleration
//            acceleration = /*acceleration * 0.9f +*/ delta
//
//            // Display a Toast message if
//            // acceleration value is over 12
//            Log.d("TAG123", "CA: $currentAcceleration; LA: $lastAcceleration; A: $acceleration")
//
//            if (currentAcceleration != 0F && lastAcceleration != 0F && abs(acceleration) > 2) {
//                if (serialShakes == 0) {
//                    Log.d("TAG123", "Shaaaaaake!!!!!!!!")
//                    Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                serialShakes++
//            } else {
//                serialShakes = 0
//            }

            gameController.moveRequest(-1 * event.values[0], event.values[1])
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
    }

    private val controllerLifecycleObserver = object : ControllerLifecycleObserver {
        override fun onDestroy() {
            unregisterAccelerometerListener()
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()

        isRunning = true

        startForegroundService()
        registerAccelerometerListener()

        gameController = DynamicOceanController(
            gameListener = DynamicOcean(applicationContext),
            screenDataRepository = DeviceScreenDataRepository(applicationContext)
        )

        gameController.setLifecycleObserver(controllerLifecycleObserver)
        gameController.createGameField()
        gameController.startGame()
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
        isRunning = false
    }
}