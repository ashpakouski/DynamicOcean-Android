package com.shpakovskiy.dynamicocean.controller

import com.shpakovskiy.dynamicocean.model.GameField
import com.shpakovskiy.dynamicocean.model.GameObject

interface GameListener {
    // Game field
    fun createGameField(gameField: GameField)
    fun resizeGameField(width: Int, height: Int, onDone: (() -> Unit)? = null)
    fun destroyGameField()

    // Game object
    fun putGameObject(gameObject: GameObject)
    fun moveObject(gameObject: GameObject)
}