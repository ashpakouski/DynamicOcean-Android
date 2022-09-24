package com.shpakovskiy.dynamicocean.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.cardview.widget.CardView

class ExpandableCard(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    fun resize(targetWidth: Int, targetHeight: Int, onExpanded: () -> Unit) {
        val initialWidth = layoutParams.width
        val initialHeight = layoutParams.height

        val widthChange = targetWidth - initialWidth
        val heightChange = targetHeight - initialHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val card = this@ExpandableCard

                card.layoutParams.width = (widthChange * interpolatedTime).toInt() + initialWidth
                card.layoutParams.height = (heightChange * interpolatedTime).toInt() + initialHeight
                card.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration = (targetHeight / context.resources.displayMetrics.density).toLong() * 3

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) = Unit
            override fun onAnimationEnd(p0: Animation?) = onExpanded()
            override fun onAnimationRepeat(p0: Animation?) = Unit
        })

        startAnimation(animation)
    }
}