package com.shpakovskiy.dynamicocean.controller

import android.util.Log
import com.shpakovskiy.dynamicocean.repository.ScreenDataRepository
import kotlin.math.abs

interface GameListener {
    // fun createField(width: Int, height: Int)
    // fun startGame()

    // fun moveObject(x: Float, y: Float)
    fun xMove(x: Float)
    fun yMove(y: Float)
    fun createOcean()
    fun destroyOcean()
}

interface GameController {
    fun startGame()
    fun moveRequest(x: Float, y: Float)
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
    companion object {
        private const val EXPANDED_FIELD_SIZE = 500
    }

    private var movingObject = MovingObject(0F, 0F, 80, 80)

    // override fun initGame() { }

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
            } else if (movingObject.x + movingObject.width + xMove > EXPANDED_FIELD_SIZE) {
                gameListener.xMove(EXPANDED_FIELD_SIZE.toFloat() - movingObject.width)
                movingObject =
                    movingObject.copy(x = EXPANDED_FIELD_SIZE.toFloat() - movingObject.width)
            } else {
                gameListener.xMove(movingObject.x + xMove)
                movingObject = movingObject.copy(x = movingObject.x + xMove)
            }

            if (movingObject.y + yMove <= 0) {
                // yMove = movingObject.y
                gameListener.yMove(0F)
                movingObject = movingObject.copy(y = 0F)
            } else if (movingObject.y + movingObject.height + yMove > EXPANDED_FIELD_SIZE) {
                gameListener.yMove(EXPANDED_FIELD_SIZE.toFloat() - movingObject.height)
                movingObject =
                    movingObject.copy(y = EXPANDED_FIELD_SIZE.toFloat() - movingObject.height)
            } else {
                gameListener.yMove(movingObject.y + yMove)
                movingObject = movingObject.copy(y = movingObject.y + yMove)
            }
        }
    }
}