package com.shpakovskiy.dynamicocean.controller

import android.util.Log
import com.shpakovskiy.dynamicocean.model.*
import com.shpakovskiy.dynamicocean.repository.GameStatRepository
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs
import kotlin.math.roundToInt

interface GameListener {
    // Game field
    fun createGameField(gameField: GameField)
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
    screenDataRepository: ScreenDataRepository,
    private val gameStatRepository: GameStatRepository
) : GameController {
    private var gameObject: GameObject
    private var gameField: GameField
    private var fieldHole: FieldHole

    init {
        val displayCutout = screenDataRepository.displayCutout
            ?: throw RuntimeException("Display cutout should not be null")
        val deviceScreen = screenDataRepository.deviceScreen
            ?: throw RuntimeException("Device screen should not be null")

        // Util
        var realCutoutHeight = displayCutout.height
        var realCutoutMargin = displayCutout.top
        if (abs(displayCutout.width / displayCutout.height - 1.0F) > 0.1F || displayCutout.top == 0.0F) {
            realCutoutHeight = displayCutout.width
            realCutoutMargin = displayCutout.bottom - realCutoutHeight
        }

        gameField = GameField(
            x = (realCutoutMargin / 2).roundToInt(),
            y = (realCutoutMargin / 2 - deviceScreen.statusBarHeight).roundToInt(),
            widthCollapsed = (displayCutout.width + realCutoutMargin).roundToInt(),
            heightCollapsed = (displayCutout.height + realCutoutMargin).roundToInt(),
            widthExpanded = (deviceScreen.width / 2 + displayCutout.width / 2).roundToInt(),
            heightExpanded = (deviceScreen.width / 2 + displayCutout.width / 2).roundToInt(),
        )

        fieldHole = FieldHole(
            x = displayCutout.left - realCutoutMargin / 2,
            y = realCutoutMargin / 2,
            width = displayCutout.width.roundToInt(),
            height = realCutoutHeight.roundToInt()
        )

        // Game object
        gameObject = GameObject(
            200F, 200F,
            displayCutout.width.roundToInt(),
            displayCutout.width.roundToInt()
        )
        gameObject = gameObject.randomizePosition()
    }

    private var gameStartMillis = 0L

    private var lifecycleObserver: ControllerLifecycleObserver? = null

    override fun createGameField() {
        gameListener.createGameField(gameField)
    }

    override fun startGame() {
        gameListener.resizeGameField(
            width = gameField.widthExpanded,
            height = gameField.heightExpanded
        ) {
            gameObject = gameObject.randomizePosition()
            gameListener.putGameObject(gameObject)
            gameStartMillis = System.currentTimeMillis()
        }
    }

    override fun finishGame() {
        val gameTime = System.currentTimeMillis() - gameStartMillis
        if (gameTime < gameStatRepository.bestAttemptTime) {
            gameStatRepository.bestAttemptTime = gameTime
        }

        destroyGameField()
    }

    override fun destroyGameField() {
        lifecycleObserver?.onDestroy()
        gameListener.resizeGameField(
            height = gameField.heightCollapsed,
            width = gameField.widthCollapsed
        ) {
            gameListener.destroyGameField()
        }
    }

    override fun moveRequest(x: Float, y: Float) {
        val acceleration = 5
        val xMove = x * acceleration
        val yMove = y * acceleration

        // Log.d("TAG123", "GoXY: [${gameObject.x}, ${gameObject.y}] DcLT: [${displayCutout.left}; ${displayCutout.top}]")
        Log.d(
            "TAG123",
            "DiffXY: [${abs(gameObject.x - fieldHole.x)}; ${abs(gameObject.y - fieldHole.y)}]"
        )

        //if (abs(gameObject.x - displayCutout.left / 2) < 10 && abs(gameObject.y - displayCutout.top / 2) < 10) {
        if (abs(gameObject.x - fieldHole.x) < 3 && abs(gameObject.y - fieldHole.y) < 3) {
            finishGame()
        } else {
            gameObject = if (gameObject.x + xMove <= 0) {
                gameObject.copy(x = 0F)
            } else if (gameObject.x + gameObject.width + xMove >= gameField.widthExpanded) {
                gameObject.copy(x = gameField.widthExpanded.toFloat() - gameObject.width)
            } else {
                gameObject.copy(x = gameObject.x + xMove)
            }

            gameObject = if (gameObject.y + yMove <= 0) {
                gameObject.copy(y = 0F)
            } else if (gameObject.y + gameObject.height + yMove > gameField.heightExpanded) {
                gameObject.copy(y = gameField.heightExpanded.toFloat() - gameObject.height)
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
            x = (0 until (gameField.widthExpanded - gameObject.width))
                .shuffled().last().toFloat(),
            y = (0 until (gameField.heightExpanded - gameObject.height))
                .shuffled().last().toFloat(),
        )
    }
}