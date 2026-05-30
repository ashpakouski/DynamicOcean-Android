package com.shpak.dynamicocean.old.controller

import com.shpak.dynamicocean.old.model.GameField
import com.shpak.dynamicocean.old.model.GameObject

interface GameListener {
    // Game field
    fun createGameField(gameField: GameField)
    fun resizeGameField(width: Int, height: Int, onDone: (() -> Unit)? = null)
    fun destroyGameField()

    // Game object
    fun putGameObject(gameObject: GameObject)
    fun moveObject(gameObject: GameObject)
}