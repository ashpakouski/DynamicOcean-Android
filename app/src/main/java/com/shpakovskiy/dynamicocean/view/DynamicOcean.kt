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
    private val movingObject: ImageView? = null
    private var gameField: ExpandableCard? = null

    override fun initGameField(posX: Int, posY: Int, defaultWidth: Int, defaultHeight: Int) {
        rootViewParams = WindowManager.LayoutParams(
            defaultWidth, defaultHeight, posX, posY,
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

    override fun createOcean() {
        create()
    }

    fun create() {
        try {
            if (rootView?.windowToken == null && rootView?.parent == null) {
                windowManager.addView(rootView, rootViewParams)
//                gameField?.expand(EXPANDED_FIELD_SIZE, EXPANDED_FIELD_SIZE) {
//                    // TODO: Add ball here
//                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun destroyOcean() {
        context.stopService(Intent(context, DynamicOceanService::class.java))
        destroy()
    }

    private fun destroy() {
        try {
            gameField?.collapse(COLLAPSED_FIELD_SIZE, COLLAPSED_FIELD_SIZE) {
                windowManager.removeView(rootView)
                rootView?.invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun xMove(x: Float) {
        movingObject?.let {
            ObjectAnimator.ofFloat(movingObject, View.X, x).apply {
                duration = 250
                start()
            }
        }
    }

    override fun yMove(y: Float) {
        movingObject?.let {
            ObjectAnimator.ofFloat(movingObject, View.Y, y).apply {
                duration = 250
                start()
            }
        }
    }
}
