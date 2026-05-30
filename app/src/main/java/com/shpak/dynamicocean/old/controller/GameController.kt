package com.shpak.dynamicocean.old.controller

interface GameController {
    // Game field
    fun createGameField()
    fun startGame()
    fun finishGame()
    fun destroyGameField()

    // Game object
    fun shiftObject(x: Float, y: Float)

    // Util
    fun setEventObserver(eventObserver: ControllerEventObserver)
}