package com.shpakovskiy.dynamicocean.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.cardview.widget.CardView


class ExpandableCard(context: Context, attrs: AttributeSet) : CardView(context, attrs) {
    fun expand(targetWidth: Int, targetHeight: Int) {
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

        startAnimation(animation)
    }

    fun collapse(targetWidth: Int, targetHeight: Int) {
        val initialWidth = layoutParams.width
        val initialHeight = layoutParams.height

        val widthChange = initialWidth - targetWidth
        val heightChange = initialHeight - targetHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val card = this@ExpandableCard

                card.layoutParams.width =
                    widthChange - (widthChange * interpolatedTime).toInt() + targetWidth
                card.layoutParams.height =
                    heightChange - (heightChange * interpolatedTime).toInt() + targetHeight
                card.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration = (initialHeight / context.resources.displayMetrics.density).toLong() * 3

        startAnimation(animation)
    }
}