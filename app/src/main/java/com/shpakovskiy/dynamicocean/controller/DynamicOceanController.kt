package com.shpakovskiy.dynamicocean.controller

import android.util.Log
import com.shpakovskiy.dynamicocean.model.DeviceScreen
import com.shpakovskiy.dynamicocean.model.DisplayCutout
import com.shpakovskiy.dynamicocean.model.GameObject
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs
import kotlin.math.roundToInt

interface GameListener {
    // Game field
    fun createGameField(x: Int, y: Int, defaultWidth: Int, defaultHeight: Int)
    fun resizeGameField(width: Int, height: Int, onDone: (() -> Unit)? = null)
    fun destroyGameField()

    // Game object
    fun putGameObject(gameObject: GameObject)
    fun replaceObject(gameObject: GameObject)
}

interface GameController {
    // Game field
    fun createGameField()
    fun startGame()
    fun finishGame()
    fun destroyGameField()

    // Game object
    fun moveRequest(x: Float, y: Float)

    // Util
    fun setLifecycleObserver(lifecycleObserver: ControllerLifecycleObserver)
}

interface ControllerLifecycleObserver {
    fun onDestroy()
}

class DynamicOceanController(
    private val gameListener: GameListener,
    screenDataRepository: ScreenDataRepository
) : GameController {
    private val displayCutout: DisplayCutout
    private val deviceScreen: DeviceScreen

    init {
        displayCutout = screenDataRepository.displayCutout
            ?: throw RuntimeException("Display cutout should not be null")
        deviceScreen = screenDataRepository.deviceScreen
            ?: throw RuntimeException("Device screen should not be null")
    }

    private var gameObject = GameObject(
        200F, 200F,
        displayCutout.width.roundToInt(),
        displayCutout.height.roundToInt()
    )

    private val expandedFieldSide =
        (deviceScreen.width / 2 + displayCutout.width + displayCutout.left).roundToInt()
    private var gameStartMillis = 0L

    private var lifecycleObserver: ControllerLifecycleObserver? = null

    override fun createGameField() {
        gameListener.createGameField(
            x = (displayCutout.left / 2).roundToInt(),
            y = (displayCutout.top / 2).roundToInt() - deviceScreen.statusBarHeight,
            defaultWidth = (displayCutout.width + displayCutout.left).roundToInt(),
            defaultHeight = (displayCutout.height + displayCutout.top).roundToInt()
        )
    }

    override fun startGame() {
        gameListener.resizeGameField(
            width = expandedFieldSide,
            height = expandedFieldSide
        ) {
            gameObject = gameObject.randomizePosition()
            gameListener.putGameObject(gameObject)
            gameStartMillis = System.currentTimeMillis()
        }
    }

    override fun finishGame() {
        val gameTime = System.currentTimeMillis() - gameStartMillis
        Log.d("TAG123", "It took: ${gameTime / 1000.0} seconds")
        destroyGameField()
    }

    override fun destroyGameField() {
        lifecycleObserver?.onDestroy()
        gameListener.resizeGameField(
            height = (displayCutout.width + displayCutout.left).roundToInt(),
            width = (displayCutout.width + displayCutout.left).roundToInt()
        ) {
            gameListener.destroyGameField()
        }
    }

    override fun moveRequest(x: Float, y: Float) {
        val acceleration = 10
        val xMove = x * acceleration
        val yMove = y * acceleration

        if (abs(gameObject.x - displayCutout.left / 2) < 2.5 && abs(gameObject.y - displayCutout.top / 2) < 2.5) {
            finishGame()
        } else {
            gameObject = if (gameObject.x + xMove <= 0) {
                gameObject.copy(x = 0F)
            } else if (gameObject.x + gameObject.width + xMove >= expandedFieldSide) {
                gameObject.copy(x = expandedFieldSide.toFloat() - gameObject.width)
            } else {
                gameObject.copy(x = gameObject.x + xMove)
            }

            gameObject = if (gameObject.y + yMove <= 0) {
                gameObject.copy(y = 0F)
            } else if (gameObject.y + gameObject.height + yMove > expandedFieldSide) {
                gameObject.copy(y = expandedFieldSide.toFloat() - gameObject.height)
            } else {
                gameObject.copy(y = gameObject.y + yMove)
            }

            gameListener.replaceObject(gameObject)
        }
    }

    override fun setLifecycleObserver(lifecycleObserver: ControllerLifecycleObserver) {
        this.lifecycleObserver = lifecycleObserver
    }

    private fun GameObject.randomizePosition(): GameObject {
        return this.copy(
            x = (0 until (expandedFieldSide - gameObject.width)).shuffled().last().toFloat(),
            y = (0 until (expandedFieldSide - gameObject.height)).shuffled().last().toFloat(),
        )
    }
}