package com.shpakovskiy.dynamicocean.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener

class DebugCircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val drawPaint = Paint()
    private var size = 0F

    init {
        drawPaint.color = Color.RED
        drawPaint.isAntiAlias = true
        setOnMeasureCallback()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(size, size, size, drawPaint)
    }

    private fun setOnMeasureCallback() {
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                removeOnGlobalLayoutListener(this)
                size = (measuredWidth / 2).toFloat()
            }
        })
    }

    private fun removeOnGlobalLayoutListener(listener: OnGlobalLayoutListener) {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}