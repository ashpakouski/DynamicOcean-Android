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
    private var gameField: ExpandableCard? = null
    // private var hole: DebugCircleView? = null

    override fun createGameField(x: Int, y: Int, defaultWidth: Int, defaultHeight: Int) {
        rootViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            x, y,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        rootViewParams?.gravity = Gravity.TOP or Gravity.START

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = layoutInflater.inflate(R.layout.dynamic_ocean, null)

        //Game field
        gameField = rootView?.findViewById(R.id.game_field)
        gameField?.layoutParams?.width = defaultWidth
        gameField?.layoutParams?.height = defaultHeight

        // Hole
        // hole = rootView?.findViewById(R.id.hole)
        // hole?.x = 512.15894F
        // hole?.y = 0.0F + (87.84109F.roundToInt() - (567.84106F.roundToInt() - 512.15894F.roundToInt()))
        // hole?.layoutParams?.height = 87.84109F.toInt()
        // hole?.layoutParams?.width = (567.84106F - 512.15894F).toInt() + 5

        try {
            if (rootView?.windowToken == null && rootView?.parent == null) {
                windowManager.addView(rootView, rootViewParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun resizeGameField(width: Int, height: Int, onDone: (() -> Unit)?) {
        gameField?.resize(width, height) {
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
        context.stopService(Intent(context, DynamicOceanService::class.java))
        destroy()
    }

    private fun destroy() {
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
