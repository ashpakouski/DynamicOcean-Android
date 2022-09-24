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
import com.shpakovskiy.dynamicocean.service.DynamicOceanService

class DynamicOcean(private val context: Context) : GameListener {
    companion object {
        private const val TAG = "DynamicOcean"
        private const val EXPANDED_FIELD_SIZE = 500
        private const val COLLAPSED_FIELD_SIZE = 120
        private const val BALL_SIZE = 80
    }

    private var rootView: View? = null
    private var rootViewParams: WindowManager.LayoutParams? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var gameObject: ImageView? = null
    private var gameField: ExpandableCard? = null

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

        try {
            if (rootView?.windowToken == null && rootView?.parent == null) {
                windowManager.addView(rootView, rootViewParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun resizeGameField(width: Int, height: Int, onDone: () -> Unit) {
        val w = if (width > gameField!!.width) width else width
        val h = if (height > gameField!!.height) height else -height

        gameField?.resize(w, h) {
            onDone()
        }
    }

    override fun putGameObject(x: Int, y: Int, width: Int, height: Int) {
        gameObject = rootView?.findViewById(R.id.game_object)
        gameObject?.x = x.toFloat()
        gameObject?.y = y.toFloat()
        gameObject?.layoutParams?.width = width
        gameObject?.layoutParams?.height = width
    }

    /*
    init {
        // Root view
        rootViewParams = WindowManager.LayoutParams(
            EXPANDED_FIELD_SIZE,
            EXPANDED_FIELD_SIZE,
            0 + FIELD_MARGIN, -135 + FIELD_MARGIN,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        rootViewParams?.gravity = Gravity.TOP or Gravity.START

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = layoutInflater.inflate(R.layout.dynamic_ocean, null)

        //Game field
        gameField = rootView.findViewById(R.id.game_field)
        gameField.layoutParams.width = COLLAPSED_FIELD_SIZE
        gameField.layoutParams.height = COLLAPSED_FIELD_SIZE

        // Moving object
        movingObject = rootView.findViewById(R.id.moving_object)
        movingObject.layoutParams.width = BALL_SIZE
        movingObject.layoutParams.height = BALL_SIZE
        movingObject.x = 0F
        movingObject.y = 0F
    }
     */

    override fun destroyOcean() {
        context.stopService(Intent(context, DynamicOceanService::class.java))
        destroy()
    }

    private fun destroy() {
        try {
            gameField?.resize(COLLAPSED_FIELD_SIZE, COLLAPSED_FIELD_SIZE) {
                windowManager.removeView(rootView)
                rootView?.invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun xMove(x: Float) {
        gameObject?.let {
            ObjectAnimator.ofFloat(gameObject, View.X, x).apply {
                duration = 250
                start()
            }
        }
    }

    override fun yMove(y: Float) {
        gameObject?.let {
            ObjectAnimator.ofFloat(gameObject, View.Y, y).apply {
                duration = 250
                start()
            }
        }
    }
}
