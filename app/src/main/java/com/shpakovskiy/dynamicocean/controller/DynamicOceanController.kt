package com.shpakovskiy.dynamicocean.controller

import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs
import kotlin.math.roundToInt

interface GameListener {
    fun xMove(x: Float)
    fun yMove(y: Float)
    fun destroyOcean()

    fun createGameField(x: Int, y: Int, defaultWidth: Int, defaultHeight: Int)
    fun resizeGameField(width: Int, height: Int, onDone: () -> Unit)

    fun putGameObject(x: Int, y: Int, width: Int, height: Int)
}

interface GameController {
    fun startGame()
    fun moveRequest(x: Float, y: Float)
    fun initGameField()
}

data class MovingObject(
    val x: Float,
    val y: Float,
    val width: Int,
    val height: Int
)

class DynamicOceanController(
    private val gameListener: GameListener,
    private val screenDataRepository: ScreenDataRepository
) : GameController {
    private val expandedOceanSize = screenDataRepository.screenWidth / 2
    private val displayCutout = screenDataRepository.displayCutout

    private var movingObject = MovingObject(
        0F,
        0F,
        screenDataRepository.displayCutout?.width!!.toInt(),
        screenDataRepository.displayCutout?.height!!.toInt()
    )

    override fun initGameField() {
        displayCutout?.let {
            gameListener.createGameField(
                x = (displayCutout.left / 2).roundToInt(),
                y = -135 + (displayCutout.top / 2).roundToInt(),
                defaultWidth = (displayCutout.width + displayCutout.left).roundToInt(),
                defaultHeight = (displayCutout.height + displayCutout.top).roundToInt()
            )
        }
    }

    override fun startGame() {
        displayCutout?.let {
            gameListener.resizeGameField(
                width = (screenDataRepository.screenWidth / 2 + displayCutout.width + displayCutout.left).roundToInt(),
                height = (screenDataRepository.screenWidth / 2 + displayCutout.width + displayCutout.left).roundToInt()
            ) {
                gameListener.putGameObject(
                    200, 200,
                    displayCutout.width.roundToInt(),
                    displayCutout.height.roundToInt()
                )
            }
        }
    }

    override fun moveRequest(x: Float, y: Float) {
        val kk = 10

        val xMove = x * kk
        val yMove = y * kk

        // Log.d("TAG123", "${abs(movingObject.x - 20F)}; ${abs(movingObject.y - 20F)}")
        if (abs(movingObject.x - 20F) < 2.5 && abs(movingObject.y - 20F) < 2.5) {
            gameListener.destroyOcean()
        } else {
            if (movingObject.x + xMove <= 0) {
                // xMove = -movingObject.x
                gameListener.xMove(0F)
                movingObject = movingObject.copy(x = 0F)
            } else if (movingObject.x + movingObject.width + xMove > expandedOceanSize) {
                gameListener.xMove(expandedOceanSize.toFloat() - movingObject.width)
                movingObject =
                    movingObject.copy(x = expandedOceanSize.toFloat() - movingObject.width)
            } else {
                gameListener.xMove(movingObject.x + xMove)
                movingObject = movingObject.copy(x = movingObject.x + xMove)
            }

            if (movingObject.y + yMove <= 0) {
                // yMove = movingObject.y
                gameListener.yMove(0F)
                movingObject = movingObject.copy(y = 0F)
            } else if (movingObject.y + movingObject.height + yMove > expandedOceanSize) {
                gameListener.yMove(expandedOceanSize.toFloat() - movingObject.height)
                movingObject =
                    movingObject.copy(y = expandedOceanSize.toFloat() - movingObject.height)
            } else {
                gameListener.yMove(movingObject.y + yMove)
                movingObject = movingObject.copy(y = movingObject.y + yMove)
            }
        }
    }
}