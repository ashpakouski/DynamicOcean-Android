package com.shpakovskiy.dynamicocean.controller

import com.shpakovskiy.dynamicocean.model.*
import com.shpakovskiy.dynamicocean.repository.GameSettingsRepository
import com.shpakovskiy.dynamicocean.repository.GameStatRepository
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class DynamicOceanController(
    private val gameListener: GameListener,
    screenDataRepository: ScreenDataRepository,
    private val gameStatRepository: GameStatRepository,
    private val gameSettingsRepository: GameSettingsRepository
) : GameController {
    // Game objects
    private var gameObject: GameObject
    private val gameField: GameField
    private val fieldHole: FieldHole

    init {
        val displayCutout = screenDataRepository.displayCutout
            ?: throw Exception("Display cutout should not be null")
        val deviceScreen = screenDataRepository.deviceScreen
            ?: throw Exception("Device screen should not be null")

        // TODO: Move model initialization logic in other model-specific places

        // Util
        //
        // Some devices (Like Galaxy A52) don't have an information about gap size between camera
        // and top of the screen, so required values have to be retrieved indirectly
        var realCutoutHeight = displayCutout.height
        var realCutoutMargin = displayCutout.top

        if (abs(displayCutout.width / displayCutout.height - 1.0F) > 0.1F || displayCutout.top == 0.0F) {
            realCutoutHeight = min(displayCutout.width, displayCutout.height)
            realCutoutMargin = displayCutout.bottom - realCutoutHeight
        }

        // Game field
        //
        // Game field is always placed in the top left corner of the screen, because "simple"
        // overlay doesn't hide status bar widgets. Most of them are placed in the right side.
        // Width is basically camera width + half of the screen. So, this value is independent
        // of camera position
        gameField = GameField(
            x = (realCutoutMargin / 2).roundToInt(),
            y = (realCutoutMargin / 2 - deviceScreen.statusBarHeight).roundToInt(),
            widthCollapsed = (displayCutout.width + realCutoutMargin).roundToInt(),
            heightCollapsed = (displayCutout.height + realCutoutMargin).roundToInt(),
            widthExpanded = (deviceScreen.width / 2 + displayCutout.width / 2).roundToInt(),
            heightExpanded = (deviceScreen.width / 2 + displayCutout.width / 2).roundToInt(),
        )

        // Field hole
        //
        // Well, it's just a square right above the camera
        fieldHole = FieldHole(
            x = displayCutout.left - realCutoutMargin / 2,
            y = realCutoutMargin / 2,
            width = displayCutout.width.roundToInt(),
            height = realCutoutHeight.roundToInt()
        )

        // Game object
        //
        // Min value for height and width are taken just to partially support devices,
        // that have "drop" or "notch" instead of hole-punch camera.
        // In other cases width is almost equal to height.
        // Non-circular cutouts are not supposed to be fully supported, as it violates game principle.
        gameObject = GameObject(
            200F, 200F,
            min(displayCutout.width, displayCutout.height).roundToInt(),
            min(displayCutout.width, displayCutout.height).roundToInt(),
            isMirrored = fieldHole.x > deviceScreen.width / 3
        )
    }

    private var gameStartMillis = 0L
    private var eventObserver: ControllerEventObserver? = null

    // Gap between object and hole to understand, that object is really "trapped"
    // TODO: Think of moving this property to FieldHole model
    private var holeGapSize: Int = 5

    override fun createGameField() {
        gameListener.createGameField(gameField)
    }

    override fun startGame() {
        gameObject = gameObject.randomizePosition()

        gameListener.resizeGameField(
            width = gameField.widthExpanded,
            height = gameField.heightExpanded
        ) {
            gameListener.putGameObject(gameObject)
            gameStartMillis = System.currentTimeMillis()
        }

        holeGapSize = gameSettingsRepository.difficulty
    }

    override fun finishGame() {
        val gameTime = System.currentTimeMillis() - gameStartMillis
        if (gameTime < gameStatRepository.bestAttemptTime) {
            gameStatRepository.bestAttemptTime = gameTime
        }

        destroyGameField()
    }

    override fun destroyGameField() {
        eventObserver?.onGameFinished()

        gameListener.resizeGameField(
            height = gameField.heightCollapsed,
            width = gameField.widthCollapsed
        ) {
            gameListener.destroyGameField()
        }
    }

    override fun shiftObject(x: Float, y: Float) {
        val acceleration = 5
        val xMove = x * acceleration
        val yMove = y * acceleration

        if (abs(gameObject.x - fieldHole.x) < holeGapSize &&
            abs(gameObject.y - fieldHole.y) < holeGapSize
        ) {
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

            gameListener.moveObject(gameObject)
        }
    }

    override fun setEventObserver(eventObserver: ControllerEventObserver) {
        this.eventObserver = eventObserver
    }

    private fun GameObject.randomizePosition(): GameObject {
        return this.copy(
            x = (0 until (gameField.widthExpanded - gameObject.width))
                .shuffled().last().toFloat(),
            y = (gameField.heightExpanded / 2 until (gameField.heightExpanded - gameObject.height))
                .shuffled().last().toFloat(),
        )
    }
}