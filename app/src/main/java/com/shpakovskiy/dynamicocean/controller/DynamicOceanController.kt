package com.shpakovskiy.dynamicocean.controller

import android.util.Log
import com.shpakovskiy.dynamicocean.model.DeviceScreen
import com.shpakovskiy.dynamicocean.model.DisplayCutout
import com.shpakovskiy.dynamicocean.model.GameObject
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs
import kotlin.math.roundToInt

interface GameListener {
    fun createGameField(x: Int, y: Int, defaultWidth: Int, defaultHeight: Int)
    fun resizeGameField(width: Int, height: Int, onDone: () -> Unit)
    fun destroyGameField()

    fun putGameObject(gameObject: GameObject)

    fun xMove(x: Float)
    fun yMove(y: Float)
}

interface GameController {
    fun createGameField()
    fun startGame()
    fun finishGame()

    fun moveRequest(x: Float, y: Float)
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

    override fun createGameField() {
        gameListener.createGameField(
            x = (displayCutout.left / 2).roundToInt(),
            y = -135 + (displayCutout.top / 2).roundToInt(),
            defaultWidth = (displayCutout.width + displayCutout.left).roundToInt(),
            defaultHeight = (displayCutout.height + displayCutout.top).roundToInt()
        )
    }

    override fun startGame() {
        gameListener.resizeGameField(
            width = expandedFieldSide,
            height = expandedFieldSide
        ) {
            gameListener.putGameObject(gameObject)
            gameStartMillis = System.currentTimeMillis()
        }
    }

    override fun finishGame() {
        val gameTime = System.currentTimeMillis() - gameStartMillis
        Log.d("TAG123", "It took: ${gameTime / 1000.0} seconds")
        gameListener.destroyGameField()
    }

    override fun moveRequest(x: Float, y: Float) {
        val kk = 10

        val xMove = x * kk
        val yMove = y * kk

        if (abs(gameObject.x - displayCutout.left / 2) < 2.5 && abs(gameObject.y - displayCutout.top / 2) < 2.5) {
            finishGame()
        } else {
            if (gameObject.x + xMove <= 0) {
                gameListener.xMove(0F)
                gameObject = gameObject.copy(x = 0F)
            } else if (gameObject.x + gameObject.width + xMove >= expandedFieldSide) {
                gameListener.xMove(expandedFieldSide.toFloat() - gameObject.width)
                gameObject = gameObject.copy(x = expandedFieldSide.toFloat() - gameObject.width)
            } else {
                gameListener.xMove(gameObject.x + xMove)
                gameObject = gameObject.copy(x = gameObject.x + xMove)
            }

            if (gameObject.y + yMove <= 0) {
                gameListener.yMove(0F)
                gameObject = gameObject.copy(y = 0F)
            } else if (gameObject.y + gameObject.height + yMove > expandedFieldSide) {
                gameListener.yMove(expandedFieldSide.toFloat() - gameObject.height)
                gameObject = gameObject.copy(y = expandedFieldSide.toFloat() - gameObject.height)
            } else {
                gameListener.yMove(gameObject.y + yMove)
                gameObject = gameObject.copy(y = gameObject.y + yMove)
            }
        }
    }
}