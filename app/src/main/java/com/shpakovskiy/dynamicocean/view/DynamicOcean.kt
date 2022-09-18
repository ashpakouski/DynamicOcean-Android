package com.shpakovskiy.dynamicocean.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.shpakovskiy.dynamicocean.R

class DynamicOcean(private val context: Context) {
    companion object {
        private const val TAG = "DynamicOcean"
        private const val FIELD_SIZE = 400
        private const val FIELD_MARGIN = 22
        private const val BALL_SIZE = 80
    }

    private val rootView: View
    private var rootViewParams: WindowManager.LayoutParams? = null
    private val windowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val movingObject: ImageView
    private val gameField: View

    init {
        rootViewParams = WindowManager.LayoutParams(
            FIELD_SIZE,
            FIELD_SIZE,
            0 + FIELD_MARGIN, -135 + FIELD_MARGIN,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = layoutInflater.inflate(R.layout.dynamic_ocean, null)

//        rootView.findViewById<View>(R.id.close).setOnClickListener {
//            Log.d("TAG123", "Click detected")
//            destroy()
//        }

        gameField = rootView.findViewById(R.id.game_field)
        movingObject = rootView.findViewById(R.id.moving_object)

        movingObject.layoutParams.width = BALL_SIZE
        movingObject.layoutParams.height = BALL_SIZE
        movingObject.x = 200F
        movingObject.y = 200F

        rootViewParams?.gravity = Gravity.TOP or Gravity.START
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun moveTo(x: Float, y: Float) {
        val kk = 10

        var xMove = x * kk
        var yMove = y * kk

        if (movingObject.x + xMove <= 0) {
            // xMove = -movingObject.x
            ObjectAnimator.ofFloat(movingObject, View.X, 0F).apply {
                duration = 250
                start()
            }
        } else if (movingObject.x + movingObject.width + xMove > 400) {
            ObjectAnimator.ofFloat(movingObject, View.X, 400F - movingObject.width).apply {
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
        } else if (movingObject.y + movingObject.height + yMove > 400) {
            ObjectAnimator.ofFloat(movingObject, View.Y, 400F - movingObject.height).apply {
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

    fun create() {
        try {
            if (rootView.windowToken == null && rootView.parent == null) {
                windowManager.addView(rootView, rootViewParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(rootView)
//            rootView.invalidate()
//            (rootView.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
