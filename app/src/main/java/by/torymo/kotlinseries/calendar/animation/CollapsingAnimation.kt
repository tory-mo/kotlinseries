package by.torymo.kotlinseries.calendar.animation

import android.view.animation.Animation
import android.view.animation.Transformation
import by.torymo.kotlinseries.calendar.CalendarView
import by.torymo.kotlinseries.calendar.CalendarViewController

class CollapsingAnimation(val view: CalendarView, private val controller: CalendarViewController, private val targetHeight: Int, private val targetGrowRadius: Int, private val down: Boolean): Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {

        val newInterpolatedTime = if(down){interpolatedTime}
                                else{1 - interpolatedTime}

        val newHeight = (targetHeight * newInterpolatedTime).toInt()
        val grow = newInterpolatedTime * (targetGrowRadius * 2)

        controller.setGrowProgress(grow)
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}