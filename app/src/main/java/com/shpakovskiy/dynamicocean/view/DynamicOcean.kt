package com.shpakovskiy.dynamicocean.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.shpakovskiy.dynamicocean.R
import kotlin.math.abs

class DynamicOcean(context: Context) {
    companion object {
        private const val TAG = "DynamicOcean"
        private const val EXPANDED_FIELD_SIZE = 500
        private const val COLLAPSED_FIELD_SIZE = 120
        private const val FIELD_MARGIN = 22
        private const val BALL_SIZE = 80
    }

    private val rootView: View
    private var rootViewParams: WindowManager.LayoutParams? = null
    private val windowManager: WindowManager
    private val movingObject: ImageView
    private val gameField: ExpandableCard

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

        // WindowManager
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun create() {
        try {
            if (rootView.windowToken == null && rootView.parent == null) {
                windowManager.addView(rootView, rootViewParams)
                gameField.expand(EXPANDED_FIELD_SIZE, EXPANDED_FIELD_SIZE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        try {
            gameField.collapse(COLLAPSED_FIELD_SIZE, COLLAPSED_FIELD_SIZE) {
                windowManager.removeView(rootView)
                rootView.invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun moveTo(x: Float, y: Float) {
        val kk = 10

        var xMove = x * kk
        var yMove = y * kk

        Log.d(TAG, "${abs(movingObject.x - 20F)}; ${abs(movingObject.y - 20F)}")
        if (abs(movingObject.x - 20F) < 2.5 && abs(movingObject.y - 20F) < 2.5) {
            destroy()
            // Toast.makeText(rootView.context, "You did it", Toast.LENGTH_SHORT).show()
        } else {
            if (movingObject.x + xMove <= 0) {
                // xMove = -movingObject.x
                ObjectAnimator.ofFloat(movingObject, View.X, 0F).apply {
                    duration = 250
                    start()
                }
            } else if (movingObject.x + movingObject.width + xMove > EXPANDED_FIELD_SIZE) {
                ObjectAnimator.ofFloat(
                    movingObject,
                    View.X,
                    EXPANDED_FIELD_SIZE.toFloat() - movingObject.width
                ).apply {
                    duration = 250
                    start()
                }
            } else {
                ObjectAnimator.ofFloat(movingObject, View.X, movingObject.x + xMove).apply {
                    duration = 250
                    start()
                }
            }

            if (movingObject.y + yMove <= 0) {
                // yMove = movingObject.y
                ObjectAnimator.ofFloat(movingObject, View.Y, 0F).apply {
                    duration = 250
                    start()
                }
            } else if (movingObject.y + movingObject.height + yMove > EXPANDED_FIELD_SIZE) {
                ObjectAnimator.ofFloat(
                    movingObject,
                    View.Y,
                    EXPANDED_FIELD_SIZE.toFloat() - movingObject.height
                ).apply {
                    duration = 250
                    start()
                }
            } else {
                ObjectAnimator.ofFloat(movingObject, View.Y, movingObject.y + yMove).apply {
                    duration = 250
                    start()
                }
            }
        }
    }
}
