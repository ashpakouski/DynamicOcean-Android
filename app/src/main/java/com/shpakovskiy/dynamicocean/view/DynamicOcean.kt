package com.shpakovskiy.dynamicocean.view

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import com.shpakovskiy.dynamicocean.R

class DynamicOcean(private val context: Context) {
    private val rootView: View
    private var rootViewParams: WindowManager.LayoutParams? = null
    private val windowManager: WindowManager
    private val layoutInflater: LayoutInflater

    init {
        rootViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0, -110,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = layoutInflater.inflate(R.layout.dynamic_ocean, null)

        rootView.findViewById<View>(R.id.close).setOnClickListener {
            Log.d("TAG123", "Click detected")
            close()
        }

        val movingObject = rootView.findViewById<View>(R.id.moving_object)
        movingObject.x = 200F
        movingObject.y = 200F

        rootViewParams?.gravity = Gravity.TOP
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun create() {
        try {
            if (rootView.windowToken == null && rootView.parent == null) {
                windowManager.addView(rootView, rootViewParams)
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(rootView)
//            rootView.invalidate()
//            (rootView.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }
}
