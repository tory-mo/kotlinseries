package by.torymo.kotlinseries.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import org.threeten.bp.LocalDate
import by.torymo.kotlinseries.calendar.animation.AnimationHandler
import kotlin.math.abs

class CalendarView: View{

    private val currentDayBgColor = Color.argb(255, 233, 84, 81)
    private val calendarTextColor = Color.argb(255, 64, 64, 64)
    private val currentSelectedDayBgColor = Color.argb(255, 219, 219, 219)
    private val multiEventIndicatorColor = Color.argb(255, 100, 68, 65)

    private var gestureDetector: GestureDetectorCompat
    private var calendarController: CalendarViewController
    private var animationHandler: AnimationHandler

    private var gestureListener : GestureDetector.SimpleOnGestureListener

    private var horizontalScrollEnabled: Boolean = true

    interface CalendarViewListener{
        fun onDayClick(dateClicked: LocalDate)
        fun onMonthScroll(displayedMonth: LocalDate)
    }

    interface CalendarAnimationListener{
        fun onOpened()
        fun onClosed()
    }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        calendarController = CalendarViewController(Paint(), OverScroller(context), Rect(), attrs, context, currentDayBgColor,
            calendarTextColor, currentSelectedDayBgColor, VelocityTracker.obtain(),
            multiEventIndicatorColor, EventsContainer())

        gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                calendarController.onSingleTapUp(e)
                invalidate()
                return super.onSingleTapUp(e)
            }

            override fun onDown(event: MotionEvent): Boolean {
                return true
            }

            override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                return true
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                if(horizontalScrollEnabled){
                    if(abs(distanceX) > 0){
                        parent.requestDisallowInterceptTouchEvent(true)
                        calendarController.onScroll(e1, e2, distanceX, distanceY)
                        invalidate()
                        return true
                    }
                }
                return false
            }
        }

        gestureDetector = GestureDetectorCompat(getContext(), gestureListener)
        animationHandler = AnimationHandler(calendarController, this)
    }

    fun getCurrentMonth(): LocalDate{
        return calendarController.getCurrentMonth()
    }

    fun setAnimationListener(listener: CalendarAnimationListener){
        animationHandler.setAnimationListener(listener)
    }

    override fun onMeasure(parentWidth: Int, parentHeight: Int) {
        super.onMeasure(parentWidth, parentHeight)

        val width = MeasureSpec.getSize(parentWidth)
        val height = MeasureSpec.getSize(parentHeight)
        if(width != 0 && height != 0) calendarController.onMeasure(width, height, paddingRight, paddingLeft)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        if(canvas != null) calendarController.onDraw(canvas)
    }

    override fun computeScroll() {
        super.computeScroll()

        if(calendarController.computeScroll()) invalidate()
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        if(visibility == GONE) return false

        return horizontalScrollEnabled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean{
        if(horizontalScrollEnabled){
            calendarController.onTouch(event)
            invalidate()
        }

        if((event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) && horizontalScrollEnabled) {
            parent.requestDisallowInterceptTouchEvent(false)
        }

        return gestureDetector.onTouchEvent(event)
    }

    private fun checkTargetHeight(){
        check(calendarController.getTargetHeight() > 0) { "Target height must be set in xml properties in order to expand/collapse CompactCalendar." }
    }

    fun setCurrentSelectedDayIndicatorStyle(style: CalendarViewController.IndicatorStyle){
        calendarController.setCurrentSelectedDayIndicatorStyle(style)
        invalidate()
    }

    fun setCurrentDayIndicatorStyle(style: CalendarViewController.IndicatorStyle){
        calendarController.setCurrentDayIndicatorStyle(style)
        invalidate()
    }

    fun setEventIndicatorStyle(eventIndicatorStyle: CalendarViewController.IndicatorStyle){
        calendarController.setEventIndicatorStyle(eventIndicatorStyle)
        invalidate()
    }

    fun setTargetHeight(targetHeight: Int){
        calendarController.setTargetHeight(targetHeight)
        checkTargetHeight()
    }

    fun showCalendar(){
        checkTargetHeight()
        animationHandler.openCalendar()
    }

    fun hideCalendar(){
        checkTargetHeight()
        animationHandler.closeCalendar()
    }

    fun showCalendarWithAnimation(){
        checkTargetHeight()
        animationHandler.openCalendarWithAnimation()
    }

    fun hideCalendarWithAnimation(){
        checkTargetHeight()
        animationHandler.closeCalendarWithAnimation()
    }

    fun scrollLeft(){
        calendarController.scrollLeft()
        invalidate()
    }

    fun scrollRight(){
        calendarController.scrollRight()
        invalidate()
    }

    fun isAnimating(): Boolean{
        return animationHandler.isAnimating()
    }

    fun shouldScrollMonth(enableHorizontalScroll: Boolean){
        this.horizontalScrollEnabled = enableHorizontalScroll
    }

    fun setCalendarBackgroundColor(bgColor: Int){
        calendarController.setCalendarBackgroundColor(bgColor)
        invalidate()
    }

    fun setCurrentSelectedDayBgColor(currentSelectedDayBgColor: Int) {
        calendarController.setCurrentSelectedDayBgColor(currentSelectedDayBgColor)
        invalidate()
    }

    fun setCurrentDayBackgroundColor(currentDayBackgroundColor: Int) {
        calendarController.setCurrentDayBackgroundColor(currentDayBackgroundColor)
        invalidate()
    }

    fun setCurrentSelectedDayTextColor(currentSelectedDayTextColor: Int) {
        calendarController.setCurrentSelectedDayTextColor(currentSelectedDayTextColor)
    }

    fun setCurrentDayTextColor(currentDayTextColor: Int) {
        calendarController.setCurrentDayTextColor(currentDayTextColor)
    }

    fun getHeightPerDay(): Int {
        return calendarController.getHeightPerDay()
    }

    fun setListener(listener: CalendarViewListener) {
        calendarController.setListener(listener)
    }

    fun shouldDrawIndicatorsBelowSelectedDays(should: Boolean){
        calendarController.shouldDrawIndicatorsBelowSelectedDays(should)
    }

    /*
    *  events methods
    * */

    fun addEvent(event: Event, shouldInvalidate: Boolean = true){
        calendarController.addEvent(event)
        if(shouldInvalidate) invalidate()
    }

    fun addEvents(events: List<Event>){
        calendarController.addEvents(events)
        invalidate()
    }

    fun getEventsForDate(datetime: LocalDate): List<Event>{
        return calendarController.getEventsForDate(datetime)
    }

    fun getEventsForMonth(datetime: LocalDate): List<Event>{
        return calendarController.getEventsForMonth(datetime)
    }

    fun removeEvent(event: Event, shouldInvalidate: Boolean = true){
        calendarController.removeEvent(event)
        if(shouldInvalidate) invalidate()
    }

    fun removeEvents(events: List<Event>){
        calendarController.removeEvents(events)
        invalidate()

    }

    fun removeAllEvents(){
        calendarController.removeAllEvents()
        invalidate()
    }

    fun removeEventsForDate(datetime: LocalDate){
        calendarController.removeEventsForDate(datetime)
    }
}