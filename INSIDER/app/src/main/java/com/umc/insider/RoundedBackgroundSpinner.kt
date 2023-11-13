package com.umc.insider
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner

class RoundedBackgroundSpinner(context: Context, attrs: AttributeSet) : AppCompatSpinner(context, attrs) {
    private val path = Path()
    private val rect = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val roundRadius = 15f
        rect.set(paddingLeft.toFloat(), paddingTop.toFloat(), (w - paddingRight).toFloat(), (h - paddingBottom).toFloat())
        path.reset()
        path.addRoundRect(rect, roundRadius, roundRadius, Path.Direction.CW)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
    }

    // Set proper elevation for the rounded spinner
    init {
        elevation = 8f
    }
}
