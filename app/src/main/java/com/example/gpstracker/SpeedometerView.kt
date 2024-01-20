package com.example.gpstracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class SpeedometerView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val speedPaint: Paint = Paint()
    private val backgroundPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val sPaint: Paint = Paint()

    private var currentSpeedMetersPerSecond: Float = 0f

    init {
        speedPaint.color = Color.GREEN
        speedPaint.style = Paint.Style.STROKE
        speedPaint.strokeWidth = 10f
        speedPaint.isAntiAlias = true

        backgroundPaint.color = Color.BLACK
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.isAntiAlias = true

        textPaint.color = Color.WHITE
        textPaint.textSize = 35f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.isAntiAlias = true

        sPaint.color = Color.WHITE
        sPaint.textSize = 80f
        sPaint.textAlign = Paint.Align.CENTER
        sPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(centerX, centerY) - speedPaint.strokeWidth / 1.5f) * 0.9f
        canvas.drawCircle(centerX, centerY, radius+5, backgroundPaint)


        val speedKmPerHour = currentSpeedMetersPerSecond * 3.6
        val sweepAngle = ((speedKmPerHour / MAX_SPEED_KM_PER_HOUR) * 360f).toFloat()

        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            -180f,
            sweepAngle,
            false,
            speedPaint
        )

        val textBounds = Rect()

        val speedText = "${speedKmPerHour.toInt()}"
        sPaint.getTextBounds(speedText, 0, speedText.length, textBounds)
        canvas.drawText(speedText, centerX, centerY, sPaint)

        val unitText = "km/h"
        textPaint.getTextBounds(unitText, 0, unitText.length, textBounds)
        canvas.drawText(unitText, centerX, centerY + textBounds.height() * 2f, textPaint)
    }

    fun setSpeedMetersPerSecond(speedMetersPerSecond: Float) {
        currentSpeedMetersPerSecond = speedMetersPerSecond
        invalidate()
    }

    companion object {
        private const val MAX_SPEED_KM_PER_HOUR = 120f
    }
}
