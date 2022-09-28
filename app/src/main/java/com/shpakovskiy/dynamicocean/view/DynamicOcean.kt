package com.shpakovskiy.dynamicocean.view

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.shpakovskiy.dynamicocean.R
import com.shpakovskiy.dynamicocean.controller.GameListener
import com.shpakovskiy.dynamicocean.model.GameField
import com.shpakovskiy.dynamicocean.model.GameObject
import com.shpakovskiy.dynamicocean.service.DynamicOceanService

class DynamicOcean(private val context: Context) : GameListener {
    companion object {
        private const val TAG = "DynamicOcean"
    }

    private var rootView: View? = null
    private var rootViewParams: WindowManager.LayoutParams? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var movingObject: ImageView? = null
    private var gameFieldCard: ExpandableCard? = null

    override fun createGameField(gameField: GameField) {
        rootViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            gameField.x, gameField.y,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        rootViewParams?.gravity = Gravity.TOP or Gravity.START

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = layoutInflater.inflate(R.layout.dynamic_ocean, null)

        //Game field
        gameFieldCard = rootView?.findViewById(R.id.game_field)
        gameFieldCard?.layoutParams?.width = gameField.widthCollapsed
        gameFieldCard?.layoutParams?.height = gameField.heightCollapsed

        try {
            if (rootView?.windowToken == null && rootView?.parent == null) {
                windowManager.addView(rootView, rootViewParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun resizeGameField(width: Int, height: Int, onDone: (() -> Unit)?) {
        gameFieldCard?.resize(width, height) {
            onDone?.invoke()
        }
    }

    override fun putGameObject(gameObject: GameObject) {
        movingObject = rootView?.findViewById(R.id.game_object)
        movingObject?.x = gameObject.x
        movingObject?.y = gameObject.y
        movingObject?.layoutParams?.width = gameObject.width
        movingObject?.layoutParams?.height = gameObject.height
    }

    override fun destroyGameField() {
        // context.stopService(Intent(context, DynamicOceanService::class.java))
        try {
            windowManager.removeView(rootView)
            rootView?.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceObject(gameObject: GameObject) {
        movingObject?.let {
            ObjectAnimator.ofFloat(movingObject, View.X, gameObject.x).apply {
                duration = 250
                start()
            }

            ObjectAnimator.ofFloat(movingObject, View.Y, gameObject.y).apply {
                duration = 250
                start()
            }
        }
    }
}
