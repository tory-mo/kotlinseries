package by.torymo.kotlinseries.calendar

import android.graphics.Canvas
import android.graphics.Paint

class CalendarDay(private var textColor: Int, paint: Paint, private var bgColor: Int = -1, private var dayIndicator: CalendarViewController.IndicatorStyle = CalendarViewController.IndicatorStyle.SMALL_INDICATOR){

    private val textPaint: Paint = Paint(paint)

    init {
        textPaint.color = textColor
    }

    fun updateTextColor(color: Int){
        textColor = color
        textPaint.color = color
    }

    fun updateBackground(color: Int){
        bgColor = color
    }

    fun getBackground(): Int{
        return bgColor
    }

    fun getIndicatorStyle(): CalendarViewController.IndicatorStyle{
        return dayIndicator
    }

    fun hasBackground(): Boolean{
        return bgColor != -1
    }

    fun drawText(canvas: Canvas, xPosition: Float, yPosition: Float, text: String){
        canvas.drawText(text, xPosition, yPosition, textPaint)
    }
}