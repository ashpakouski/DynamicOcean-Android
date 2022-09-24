package com.shpakovskiy.dynamicocean.controller

import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs
import kotlin.math.roundToInt

interface GameListener {
    fun xMove(x: Float)
    fun yMove(y: Float)
    fun createOcean()
    fun destroyOcean()
    fun initGameField(posX: Int, posY: Int, defaultWidth: Int, defaultHeight: Int)
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
            gameListener.initGameField(
                posX = (displayCutout.left / 2).roundToInt(),
                posY = -135 + (displayCutout.top / 2).roundToInt(),
                defaultWidth = (displayCutout.width + displayCutout.left).roundToInt(),
                defaultHeight = (displayCutout.height + displayCutout.top).roundToInt()
            )
        }
    }

    override fun startGame() {
        gameListener.createOcean()
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