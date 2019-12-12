package by.torymo.kotlinseries.calendar.animation

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.Animation
import by.torymo.kotlinseries.calendar.CalendarView
import by.torymo.kotlinseries.calendar.CalendarViewController
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.sqrt
import android.view.animation.OvershootInterpolator




class AnimationHandler(private val calendarController: CalendarViewController, private val calendarView: CalendarView) {

    private val HEIGHT_ANIM_DURATION_MILLIS = 650L
    private val INDICATOR_ANIM_DURATION_MILLIS = 600L
    private var isAnimating = false
    lateinit var  calendarAnimationListener: CalendarView.CalendarAnimationListener

    fun setAnimationListener(listener: CalendarView.CalendarAnimationListener){
        this.calendarAnimationListener = listener
    }

    fun isAnimating(): Boolean = isAnimating

    fun openCalendar(){
        if(isAnimating) return

        isAnimating = true
        val heightAnim = getCollapsingAnimation(true)
        heightAnim.duration = HEIGHT_ANIM_DURATION_MILLIS
        heightAnim.interpolator = AccelerateDecelerateInterpolator()
        calendarController.setAnimationStatus(CalendarViewController.EXPAND_COLLAPSE_CALENDAR)
        setUpAnimationListenerForOpen(heightAnim)
        calendarView.layoutParams.height = 0
        calendarView.requestLayout()
        calendarView.startAnimation(heightAnim)
    }

    fun closeCalendar(){
        if(isAnimating) return

        isAnimating = true
        val heightAnim = getCollapsingAnimation(false)
        heightAnim.duration = HEIGHT_ANIM_DURATION_MILLIS
        heightAnim.interpolator = AccelerateDecelerateInterpolator()
        setUpAnimationListenerForOpen(heightAnim)
        calendarController.setAnimationStatus(CalendarViewController.EXPAND_COLLAPSE_CALENDAR)
        calendarView.layoutParams.height = calendarView.height
        calendarView.requestLayout()
        calendarView.startAnimation(heightAnim)
    }

    fun openCalendarWithAnimation(){
        if(isAnimating) return

        isAnimating = true
        val indicatorAnim = getIndicatorAnimator(1f, calendarController.getDayIndicatorRadius())
        val heightAnim = getExposeCollapsingAnimation(true)
        calendarView.layoutParams.height = 0
        calendarView.requestLayout()
        setUpAnimationListenerForExposeOpen(indicatorAnim, heightAnim)
        calendarView.startAnimation(heightAnim)

    }

    fun closeCalendarWithAnimation(){
        if(isAnimating) return

        isAnimating = true
        val indicatorAnim = getIndicatorAnimator(calendarController.getDayIndicatorRadius(), 1f)
        val heightAnim = getExposeCollapsingAnimation(false)
        calendarView.layoutParams.height = calendarView.height
        calendarView.requestLayout()
        setUpAnimationListenerForExposeClose(indicatorAnim, heightAnim)
        calendarView.startAnimation(heightAnim)
    }

    private fun getIndicatorAnimator(from: Float, to: Float): Animator{
        val animIndicator = ValueAnimator.ofFloat(from, to)
        animIndicator.duration = INDICATOR_ANIM_DURATION_MILLIS
        animIndicator.interpolator = OvershootInterpolator()
        animIndicator.addUpdateListener {animation->
            calendarController.setGrowFactorIndicator(animation.animatedFraction)
            calendarView.invalidate()
        }
        return animIndicator
    }

    private fun getExposeCollapsingAnimation(isCollapsing: Boolean): Animation{
        val heightAnim = getCollapsingAnimation(isCollapsing)
        heightAnim.duration = HEIGHT_ANIM_DURATION_MILLIS
        heightAnim.interpolator = AccelerateDecelerateInterpolator()
        return heightAnim
    }

    private fun getCollapsingAnimation(isCollapsing: Boolean): Animation{
        return CollapsingAnimation(calendarView, calendarController, calendarController.getTargetHeight(), getTargetGrowRadius(), isCollapsing)
    }

    private fun getTargetGrowRadius(): Int{
        val heightSq = calendarController.getTargetHeight() * calendarController.getTargetHeight()
        val widthSq = calendarController.getWidth() * calendarController.getWidth()
        return (0.5 * sqrt((heightSq + widthSq).toDouble())).toInt()
    }

    private fun onOpen(){
        calendarAnimationListener?.onOpened()
    }

    private fun onClose(){
        calendarAnimationListener?.onClosed()
    }

    private fun setUpAnimationListenerForOpen(openAnimation: Animation){
        openAnimation.setAnimationListener(object : AnimationListener() {
            override fun onAnimationEnd(p0: Animation?) {
                onOpen()
                isAnimating = false
            }
        })
    }

    private fun setUpAnimationListenerForExposeOpen(indicatorAnimation: Animator, heightAnim: Animation){
        heightAnim.setAnimationListener(object :  AnimationListener(){
            override fun onAnimationEnd(p0: Animation?) {
                indicatorAnimation.start()
            }
            override fun onAnimationStart(p0: Animation?) {
                calendarController.setAnimationStatus(CalendarViewController.EXPOSE_CALENDAR_ANIMATION)
            }

        })
        indicatorAnimation.addListener(object : AnimatorListener(){
            override fun onAnimationStart(p0: Animator?) {
                calendarController.setAnimationStatus(CalendarViewController.ANIMATE_INDICATORS)
            }
            override fun onAnimationEnd(p0: Animator?) {
                calendarController.setAnimationStatus(CalendarViewController.IDLE)
                onOpen()
                isAnimating = false
            }
        })
    }

    private fun setUpAnimationListenerForExposeClose(indicatorAnimation: Animator, heightAnim: Animation){
        heightAnim.setAnimationListener(object :  AnimationListener(){
            override fun onAnimationEnd(p0: Animation?) {
                calendarController.setAnimationStatus(CalendarViewController.IDLE)
                onClose()
                isAnimating = false
            }
            override fun onAnimationStart(p0: Animation?) {
                calendarController.setAnimationStatus(CalendarViewController.EXPOSE_CALENDAR_ANIMATION)
                indicatorAnimation.start()
            }

        })
        indicatorAnimation.addListener(object : AnimatorListener(){
            override fun onAnimationStart(p0: Animator?) {
                calendarController.setAnimationStatus(CalendarViewController.ANIMATE_INDICATORS)
            }

        })
    }
}
