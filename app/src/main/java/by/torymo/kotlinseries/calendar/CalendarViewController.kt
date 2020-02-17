package by.torymo.kotlinseries.calendar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.OverScroller
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import org.threeten.bp.LocalDate
import kotlin.math.sqrt
import android.view.MotionEvent
import org.threeten.bp.DayOfWeek
import java.util.*
import kotlin.math.abs
import kotlin.math.round

class CalendarViewController(private var dayPaint: Paint, private val scroller: OverScroller, private val textSizeRect: Rect, attrs: AttributeSet?, context: Context?, private var currentDayBgColor: Int,
                             private var calendarTextColor: Int, private var currentSelectedDayBgColor: Int, private var velocityTracker: VelocityTracker?, private var multiEventIndicatorColor: Int,
                             private val eventsContainer: EventsContainer) {

    companion object{
        const val IDLE  = 0
        const val EXPOSE_CALENDAR_ANIMATION  = 1
        const val EXPAND_COLLAPSE_CALENDAR   = 2
        const val ANIMATE_INDICATORS = 3
    }

    enum class IndicatorStyle(val type: Int){
        SMALL_INDICATOR(3), NO_FILL_LARGE_INDICATOR(2), FILL_LARGE_INDICATOR(1);

        companion object {
            fun toIndicatorStyle(value: Int): IndicatorStyle {
                return when (value) {
                    1 -> FILL_LARGE_INDICATOR
                    2 -> NO_FILL_LARGE_INDICATOR
                    else -> SMALL_INDICATOR
                }
            }
        }
    }

    private val VELOCITY_UNIT_PIXELS_PER_SECOND  = 1000
    private val LAST_FLING_THRESHOLD_MILLIS   = 300
    private val SNAP_VELOCITY_DIP_PER_SECOND = 400.0f
    private val ANIMATION_SCREEN_SET_DURATION_MILLIS    = 700.0f
    private val daysInWeek: Int = 7

    private var calendarHeaderTextColor: Int = 0
    private var currentDayTextColor: Int = 0
    private var otherMonthDaysTextColor: Int = 0
    private var currentSelectedDayTextColor: Int = 0
    private var calendarBgColor: Int = 0
    private var textSize: Int = 30
    private var targetHeight: Int = 0
    private var eventIndicatorStyle: IndicatorStyle = IndicatorStyle.SMALL_INDICATOR
    private var currentDayIndicatorStyle: IndicatorStyle = IndicatorStyle.FILL_LARGE_INDICATOR
    private var currentSelectedDayIndicatorStyle: IndicatorStyle = IndicatorStyle.FILL_LARGE_INDICATOR
    private var displayOtherMonthDays: Boolean = false
    private var shouldDrawIndicatorsBelowSelectedDays: Boolean = false
    private var textHeight: Int
    private var textWidth: Int
    private var widthPerDay: Int = 0
    private var heightPerDay: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private var paddingLeft: Int = 0
    private var paddingRight: Int = 0
    private var paddingWidth: Int = 40
    private var paddingHeight: Int = 40
    private var monthsScrolledSoFar: Int = 0
    private var maximumVelocity: Int = 0
    private var densityAdjustedSnapVelocity: Int = 0
    private var distanceThresholdForAutoScroll: Int = 0
    private var animationStatus: Int = 0
    private var xIndicatorOffset: Float = 0.0f
    private var multiDayIndicatorStrokeWidth: Int = 0
    private var bigCircleIndicatorRadius: Float = 0.0f
    private var smallIndicatorRadius: Float = 0.0f
    private var screenDensity: Float = 1.0f
    private var distanceX: Float = 0.0f

    private val today: LocalDate = LocalDate.now()
    private var currentCalendar: LocalDate = DateTimeUtils.now()
    private val firstDayOfWeekToDraw = DayOfWeek.MONDAY.value
    private val firstDayOfWeekToDrawCalendar = Calendar.MONDAY

    private var growFactorIndicator: Float = 0.0f
    private var growFactor: Float = Float.MAX_VALUE

    private var currentDirection: Direction = Direction.NONE
    private val accumulatedScrollOffset: PointF = PointF()
    private val background: Paint = Paint()

    private var isScrolling = false
    private var isSmoothScrolling = false

    private var lastAutoScrollFromFling: Long = 0

    private var isRtl = false

    private enum class Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    private val dayColumnNames = listOf("M", "T", "W", "T", "F", "S", "S")

    private var calendarViewListener: CalendarView.CalendarViewListener? = null


    private val ordinaryDay: CalendarDay
    private val todayDay: CalendarDay
    private val selectedDay: CalendarDay
    private val otherMonthDay: CalendarDay
    private val header: CalendarDay

    init{
        loadAttributes(attrs, context)

        dayPaint.textAlign = Paint.Align.CENTER

        dayPaint.flags = Paint.ANTI_ALIAS_FLAG
        dayPaint.textSize = textSize.toFloat()
        dayPaint.getTextBounds("31", 0, "31".length, textSizeRect)
        dayPaint.style = Paint.Style.FILL

        textHeight = textSizeRect.height() * 3
        textWidth = textSizeRect.width() * 2

        initScreenDensityRelatedValues(context)

        xIndicatorOffset = 3.5f * screenDensity
        smallIndicatorRadius = 2.5f * screenDensity

        ordinaryDay = CalendarDay(calendarTextColor, dayPaint)
        header = CalendarDay(calendarHeaderTextColor, dayPaint)
        otherMonthDay = CalendarDay(otherMonthDaysTextColor, dayPaint)
        todayDay = CalendarDay(currentDayTextColor, dayPaint, currentDayBgColor, currentDayIndicatorStyle)
        selectedDay = CalendarDay(currentSelectedDayTextColor, dayPaint, currentSelectedDayBgColor, currentSelectedDayIndicatorStyle)
    }

    fun getDayIndicatorRadius(): Float{
        return bigCircleIndicatorRadius
    }

    fun setEventIndicatorStyle(style: IndicatorStyle){
        this.eventIndicatorStyle = style
    }

    fun setCurrentDayIndicatorStyle(style: IndicatorStyle){
        this.currentDayIndicatorStyle = style
    }

    fun setCurrentSelectedDayIndicatorStyle(style: IndicatorStyle){
        this.currentSelectedDayIndicatorStyle = style
    }

    fun getTargetHeight(): Int{
        return targetHeight
    }

    fun setTargetHeight(targetHeight: Int){
        this.targetHeight = targetHeight
    }

    fun getGrowFactor():Float {
        return growFactor
    }

    fun setGrowProgress(grow: Float) {
        growFactor = grow
    }

    fun setGrowFactorIndicator(growfactorIndicator: Float) {
        this.growFactorIndicator = growfactorIndicator
    }

    fun getGrowFactorIndicator(): Float {
        return growFactorIndicator
    }


    fun getWidth(): Int{
        return width
    }

    fun setAnimationStatus(animationStatus: Int) {
        this.animationStatus = animationStatus
    }

    fun setCurrentDate(date: LocalDate) {
        distanceX = 0f
        monthsScrolledSoFar = 0
        accumulatedScrollOffset.x = 0f
        scroller.startScroll(0, 0, 0, 0)

        currentCalendar = date.withDayOfMonth(1)
    }

    fun setCalendarBackgroundColor(bgColor: Int) {
        this.calendarBgColor = bgColor
    }

    fun setCurrentDayBackgroundColor(currentDayBackgroundColor: Int) {
        this.currentDayBgColor = currentDayBackgroundColor
    }

    fun setCurrentSelectedDayTextColor(currentSelectedDayTextColor: Int){
        this.currentSelectedDayTextColor = currentSelectedDayTextColor
    }

    fun setCurrentDayTextColor(currentDayTextColor: Int) {
        this.currentDayTextColor = currentDayTextColor
    }
    fun setCurrentSelectedDayBgColor(currentSelectedDayBgColor: Int) {
        this.currentSelectedDayBgColor = currentSelectedDayBgColor
    }

    fun setListener(listener: CalendarView.CalendarViewListener){
        this.calendarViewListener = listener
    }

    fun getHeightPerDay(): Int {
        return heightPerDay
    }

    fun shouldDrawIndicatorsBelowSelectedDays(should: Boolean){
        this.shouldDrawIndicatorsBelowSelectedDays = should
    }


    private fun performMonthScrollCallback() {
        calendarViewListener?.onMonthScroll(getCurrentMonth())
    }


    // Add a little leeway buy checking if amount scrolled is almost same as expected scroll
    // as it maybe off by a few pixels
    private fun isScrolling(): Boolean {
        val scrolledX = abs(accumulatedScrollOffset.x)
        val expectedScrollX = abs(width * monthsScrolledSoFar)
        return scrolledX < expectedScrollX - 5 || scrolledX > expectedScrollX + 5
    }


    private fun loadAttributes(attrs: AttributeSet?, context: Context?){
        if(attrs == null || context == null) return

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CompactCalendarView, 0, 0)
        try{
            currentDayBgColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayBackgroundColor, currentDayBgColor)
            calendarTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarTextColor, calendarTextColor)
            calendarHeaderTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarHeaderTextColor, calendarTextColor)

            currentDayTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayTextColor, calendarTextColor)
            otherMonthDaysTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarOtherMonthDaysTextColor, calendarTextColor)

            currentSelectedDayBgColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBgColor)
            currentSelectedDayTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayTextColor, calendarTextColor)
            calendarBgColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarBackgroundColor, calendarBgColor)
            multiEventIndicatorColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarMultiEventIndicatorColor, multiEventIndicatorColor)
            textSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat(), context.resources.displayMetrics).toInt())
            targetHeight = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTargetHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight.toFloat(), context.resources.displayMetrics).toInt())
            eventIndicatorStyle = IndicatorStyle.toIndicatorStyle(typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarEventIndicatorStyle, eventIndicatorStyle.type))
            currentDayIndicatorStyle = IndicatorStyle.toIndicatorStyle(typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarCurrentDayIndicatorStyle, currentDayIndicatorStyle.type))
            currentSelectedDayIndicatorStyle = IndicatorStyle.toIndicatorStyle(typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayIndicatorStyle, currentSelectedDayIndicatorStyle.type))
            displayOtherMonthDays = typedArray.getBoolean(R.styleable.CompactCalendarView_compactCalendarDisplayOtherMonthDays, displayOtherMonthDays)

        }finally {
            typedArray.recycle()
        }
    }

    private fun initScreenDensityRelatedValues(context: Context?){
        if(context == null) return

        screenDensity = context.resources.displayMetrics.density
        val configuration = ViewConfiguration.get(context)
        densityAdjustedSnapVelocity = (screenDensity * SNAP_VELOCITY_DIP_PER_SECOND).toInt()
        maximumVelocity = configuration.scaledMaximumFlingVelocity
        multiDayIndicatorStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, context.resources.displayMetrics).toInt()
    }

    fun onMeasure(width: Int, height: Int, paddingRight: Int, paddingLeft: Int){
        widthPerDay = width/daysInWeek
        heightPerDay = if(targetHeight > 0) targetHeight / 7 else height / 7
        this.width = width
        this.height = height
        this.paddingRight = paddingRight
        this.paddingLeft = paddingLeft
        distanceThresholdForAutoScroll = (width * 0.50).toInt()

        //makes easier to find radius
        bigCircleIndicatorRadius = getInterpolatedBigCircleIndicator()

        // scale the selected day indicators slightly so that event indicators can be drawn below
        bigCircleIndicatorRadius = if (shouldDrawIndicatorsBelowSelectedDays && eventIndicatorStyle == IndicatorStyle.SMALL_INDICATOR) bigCircleIndicatorRadius * 0.85f else bigCircleIndicatorRadius
    }

    fun onFling(): Boolean{
        scroller.forceFinished(true)
        return true
    }

    fun onDown(): Boolean{
        scroller.forceFinished(true)
        return true
    }

    fun computeScroll(): Boolean{
        if(scroller.computeScrollOffset()){
            accumulatedScrollOffset.x = scroller.currX.toFloat()
            return true
        }
        return false
    }

    fun onDraw(canvas: Canvas){
        paddingWidth = widthPerDay / 2
        paddingHeight = heightPerDay / 2
        calculateXPositionOffset()

        when(animationStatus){
            EXPOSE_CALENDAR_ANIMATION ->{
                drawCalendarWhileAnimating(canvas)
            }
            ANIMATE_INDICATORS ->{
                drawCalendarWhileAnimatingIndicators(canvas)
            }
            else ->{
                drawCalenderBackground(canvas)
                drawScrollableCalender(canvas)
            }
        }
    }

    private fun drawCalenderBackground(canvas: Canvas){
        dayPaint.color = calendarBgColor
        dayPaint.style = Paint.Style.FILL
        canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), dayPaint)
        dayPaint.style = Paint.Style.STROKE
        dayPaint.color = calendarTextColor
    }

    private fun drawCalendarWhileAnimatingIndicators(canvas: Canvas){
        dayPaint.color = calendarBgColor
        dayPaint.style = Paint.Style.FILL
        canvas.drawCircle(0.0f, 0.0f, growFactor, dayPaint)
        dayPaint.style = Paint.Style.STROKE
        dayPaint.color = Color.WHITE
        drawScrollableCalender(canvas)
    }

    private fun drawCalendarWhileAnimating(canvas: Canvas){
        background.color = calendarBgColor
        background.style = Paint.Style.FILL
        canvas.drawCircle(0.0f, 0.0f, growFactor, background)
        dayPaint.style = Paint.Style.STROKE
        dayPaint.color = Color.WHITE
        drawScrollableCalender(canvas)
    }

    private fun drawScrollableCalender(canvas: Canvas){
        if(isRtl){
            drawNextMonth(canvas, -1)
            drawCurrentMonth(canvas)
            drawPreviousMonth(canvas,1)
        }else{
            drawPreviousMonth(canvas, -1)
            drawCurrentMonth(canvas)
            drawNextMonth(canvas, 1)
        }
    }

    private fun drawCurrentMonth(canvas: Canvas){
        drawMonth(canvas, toMonthWithOffset(today, monthsScrolledSoFar()), width * -monthsScrolledSoFar)
    }

    private fun drawNextMonth(canvas: Canvas, offset: Int){
        drawMonth(canvas, toMonthWithOffset(today, -monthsScrolledSoFar + offset), (width * (-monthsScrolledSoFar + 1)))
    }

    private fun drawPreviousMonth(canvas: Canvas, offset: Int){
        drawMonth(canvas, toMonthWithOffset(today, -monthsScrolledSoFar + offset), (width * (-monthsScrolledSoFar - 1)))
    }

    fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean{
        if(isSmoothScrolling) return true

        if(currentDirection == Direction.NONE){
            currentDirection = if(abs(distanceX) > abs(distanceY)) Direction.HORIZONTAL
            else Direction.VERTICAL
        }

        isScrolling = true

        this.distanceX = distanceX
        return true
    }

    fun onSingleTapUp(e: MotionEvent?){
        if(isScrolling()) return

        val dayColumn = round((paddingLeft + (e?.x ?: 0f) - paddingWidth - paddingRight) / widthPerDay)
        val dayRow = round(((e?.y ?: 0f) - paddingHeight) / heightPerDay)

        val calendarWithFirstDayOfMonth = toMonthWithOffset(today, monthsScrolledSoFar())
        val firstDayOfMonth = getDayOfWeek(calendarWithFirstDayOfMonth)

        val dayOfMonth = (dayRow - 1) * 7 - firstDayOfMonth + if (isRtl) {
                (6 - dayColumn).toInt()
            } else {
                dayColumn.toInt()
            }

        if (dayOfMonth < calendarWithFirstDayOfMonth.month.maxLength() && dayOfMonth >= 0) {
            currentCalendar = calendarWithFirstDayOfMonth.plusDays(dayOfMonth.toLong())

            performOnDayClickCallback(currentCalendar)
        }
    }

    private fun performOnDayClickCallback(datetime: LocalDate){
        calendarViewListener?.onDayClick(datetime)
    }

    private fun monthsScrolledSoFar(): Int {
        return if (isRtl) monthsScrolledSoFar else -monthsScrolledSoFar
    }

    fun onTouch(event: MotionEvent): Boolean{
        if(velocityTracker == null) velocityTracker = VelocityTracker.obtain()

        velocityTracker!!.addMovement(event)

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                if(!scroller.isFinished) scroller.abortAnimation()
                isSmoothScrolling = false
            }
            MotionEvent.ACTION_MOVE->{
                velocityTracker!!.addMovement(event)
                velocityTracker!!.computeCurrentVelocity(500)
            }
            MotionEvent.ACTION_UP->{
                handleHorizontalScrolling()
                velocityTracker!!.recycle()
                velocityTracker!!.clear()
                velocityTracker = null
                isScrolling = false
            }
        }

        return false
    }

    private fun handleHorizontalScrolling(){
        val velocityX = computeVelocity()
        handleSmoothScrolling(velocityX)
        currentDirection = Direction.NONE

        currentCalendar = toMonthWithOffset(today, monthsScrolledSoFar())
    }

    private fun computeVelocity(): Int{
        velocityTracker?.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, maximumVelocity.toFloat())
        return velocityTracker?.xVelocity?.toInt() ?: 0
    }

    private fun handleSmoothScrolling(velocityX: Int){
        val distanceScrolled = accumulatedScrollOffset.x - (width * monthsScrolledSoFar)
        val isEnoughTimeElapsedSinceLastSmoothScroll = System.currentTimeMillis() - lastAutoScrollFromFling > LAST_FLING_THRESHOLD_MILLIS
        if((velocityX > densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) || (isScrolling && distanceScrolled > distanceThresholdForAutoScroll)) scrollPrevMonth()
        else if((velocityX < -densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) || (isScrolling && distanceScrolled < -distanceThresholdForAutoScroll)) scrollNextMonth()
        else{
            isSmoothScrolling = false
            snapBackScroller()
        }
    }

    private fun snapBackScroller(){
        val remainingScrollAfterFingerLifted = accumulatedScrollOffset.x - (monthsScrolledSoFar * width)
        scroller.startScroll(accumulatedScrollOffset.x.toInt(), 0,  -remainingScrollAfterFingerLifted.toInt(), 0)
    }

    fun scrollLeft(){
        if(isRtl) scrollNext()
        else scrollPrev()
    }

    fun scrollRight(){
        if(isRtl) scrollPrev()
        else scrollNext()
    }

    private fun scrollNext(){
        monthsScrolledSoFar--
        accumulatedScrollOffset.x = (monthsScrolledSoFar * width).toFloat()
        setCurrentDate(currentCalendar.plusMonths(1))

        performMonthScrollCallback()
    }

    private fun scrollPrev(){
        monthsScrolledSoFar++
        accumulatedScrollOffset.x = (monthsScrolledSoFar * width).toFloat()
        setCurrentDate(currentCalendar.minusMonths(1))
        performMonthScrollCallback()
    }

    private fun scrollNextMonth(){
        lastAutoScrollFromFling = System.currentTimeMillis()
        monthsScrolledSoFar--
        performScroll()
        isSmoothScrolling = true
        performMonthScrollCallback()
    }



    private fun scrollPrevMonth(){
        lastAutoScrollFromFling = System.currentTimeMillis()
        monthsScrolledSoFar++
        performScroll()
        isSmoothScrolling = true
        performMonthScrollCallback()
    }

    private fun performScroll(){
        val targetScroll = monthsScrolledSoFar * width
        val remainingScrollAfterFingerLifted = targetScroll - accumulatedScrollOffset.x
        scroller.startScroll(
            accumulatedScrollOffset.x.toInt(), 0, remainingScrollAfterFingerLifted.toInt(), 0,
            (abs(remainingScrollAfterFingerLifted.toInt()) / width * ANIMATION_SCREEN_SET_DURATION_MILLIS).toInt()
        )
    }



    private fun drawMonth(canvas: Canvas, monthToDraw: LocalDate, offset: Int){
        drawEvents(canvas, monthToDraw, offset)

        val firstDayOfMonth: Int = getDayOfWeek(monthToDraw)
        val isSameMonthAndYearAsToday = (monthToDraw.monthValue == today.monthValue) && (monthToDraw.year == today.year)
        val isSameMonthAndYearAsCurrentCalendar = (monthToDraw.monthValue == currentCalendar.monthValue) && (monthToDraw.year == currentCalendar.year)

        val todayDayOfMonth = today.dayOfMonth
        val isAnimatingWithExpose = animationStatus == EXPOSE_CALENDAR_ANIMATION

        val maximumMonthDay = monthToDraw.lengthOfMonth()
        val maximumPreviousMonthDay = monthToDraw.minusMonths(1).lengthOfMonth()

        var dayColumn = 0
        var colDirection = if (isRtl) 6 else 0
        var dayRow = 0

        while (dayColumn <= 6) {

            if (dayRow == 7) {
                if (isRtl) {
                    colDirection--
                } else {
                    colDirection++
                }
                dayRow = 0
                if (dayColumn <= 6) {
                    dayColumn++
                }
            }
            if (dayColumn == dayColumnNames.size) {
                break
            }
            val xPosition = (widthPerDay * dayColumn + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight)
            val yPosition = (dayRow * heightPerDay + paddingHeight).toFloat()
            if (xPosition >= growFactor && (isAnimatingWithExpose || animationStatus == ANIMATE_INDICATORS) || yPosition >= growFactor){
                // don't draw days if animating expose or indicators
                dayRow++
                continue
            }
            if (dayRow == 0) {
                // first row, so draw the first letter of the day
                header.drawText(canvas,  xPosition, paddingHeight.toFloat(), dayColumnNames[colDirection])
            } else {
                val day = (dayRow - 1) * 7 + colDirection + 1 - firstDayOfMonth

                if (day <= 0 || day > maximumMonthDay) {
                    if (displayOtherMonthDays) {
                        val txt = if(day <= 0) (maximumPreviousMonthDay + day).toString() else (day - maximumMonthDay).toString()
                        // Display day month before
                        otherMonthDay.drawText(canvas, xPosition, yPosition, txt)
                    }
                } else {
                    val dayToDraw=
                        if (isSameMonthAndYearAsToday && todayDayOfMonth == day && !isAnimatingWithExpose) {
                            todayDay
                        }else if (currentCalendar.dayOfMonth == day && isSameMonthAndYearAsCurrentCalendar && !isAnimatingWithExpose) {
                            selectedDay
                        }else{
                            ordinaryDay
                        }
                    if(dayToDraw.hasBackground())
                        drawDayCircleIndicator(dayToDraw.getIndicatorStyle(), canvas, xPosition, yPosition, dayToDraw.getBackground())
                    dayToDraw.drawText(canvas, xPosition, yPosition, day.toString())
                }
            }
            dayRow++
        }
    }

    private fun drawEvents(canvas: Canvas, monthToDraw: LocalDate, offset: Int){
        val currentMonth = monthToDraw.monthValue
        val uniqueEvents = eventsContainer.getEventsForMonthAndYear(currentMonth, monthToDraw.year)
        val shouldDrawCurrentDayCircle = currentMonth == today.monthValue
        val shouldDrawSelectedDayCircle = currentMonth == currentCalendar.monthValue

        val todayDayOfMonth = today.dayOfMonth
        val currentYear = today.year
        val selectedDayOfMonth = currentCalendar.dayOfMonth
        val indicatorOffset = bigCircleIndicatorRadius / 2

        uniqueEvents.forEach {
            var dayOfWeek: Int = getDayOfWeek(it.timeInMillis)
            if(isRtl) dayOfWeek = 6 - dayOfWeek

            val weekNumberForMonth = weekOfMonth(it.timeInMillis)

            val xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight
            var yPosition = (weekNumberForMonth * heightPerDay + paddingHeight).toFloat()

            if (!((((animationStatus == EXPOSE_CALENDAR_ANIMATION || animationStatus == ANIMATE_INDICATORS) && xPosition >= growFactor ) || yPosition >= growFactor)
                || (animationStatus == EXPAND_COLLAPSE_CALENDAR && yPosition >= growFactor)
                    || (animationStatus == EXPOSE_CALENDAR_ANIMATION && (eventIndicatorStyle == IndicatorStyle.FILL_LARGE_INDICATOR || eventIndicatorStyle == IndicatorStyle.NO_FILL_LARGE_INDICATOR)))) {
                val events = it.events
                val dayOfMonth = it.timeInMillis.dayOfMonth
                val eventYear = it.timeInMillis.year
                val isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && todayDayOfMonth == dayOfMonth && eventYear == currentYear
                val isCurrentSelectedDay = shouldDrawSelectedDayCircle && selectedDayOfMonth == dayOfMonth

                if (shouldDrawIndicatorsBelowSelectedDays || (!shouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay) || animationStatus == EXPOSE_CALENDAR_ANIMATION) {
                    if (!(eventIndicatorStyle == IndicatorStyle.FILL_LARGE_INDICATOR || eventIndicatorStyle == IndicatorStyle.NO_FILL_LARGE_INDICATOR)) {
                        yPosition += indicatorOffset
                        // offset event indicators to draw below selected day indicators
                        // this makes sure that they do no overlap
                        if (shouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
                            yPosition += indicatorOffset
                        }
                    }

                    if (events.isNotEmpty()) {
                        drawEventIndicatorCircle(canvas, xPosition, yPosition, events[0].color)
                    }
                }
            }
        }
    }

    private fun weekOfMonth(date: LocalDate): Int{
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = firstDayOfWeekToDrawCalendar
        calendar.timeInMillis = DateTimeUtils.toMilliseconds(date)
        return calendar.get(Calendar.WEEK_OF_MONTH)

        // this code doesn't work in some cases
        /*val weekFields = WeekFields.of(Locale.getDefault())
        var week = date.get(weekFields.weekOfMonth())
        if(date.withDayOfMonth(1).dayOfWeek.value == 1 && date.dayOfMonth % 7 == 0) week--
        return week*/
    }

    private fun getInterpolatedBigCircleIndicator(): Float{
        val x0: Float = textSizeRect.height().toFloat()
        val x1: Float = heightPerDay.toFloat() // take into account indicator offset
        val x = (x1 + textSizeRect.height()) / 2f // pick a point which is almost half way through heightPerDay and textSizeRect
        val y1 = 0.5 * sqrt((x1 * x1) + (x1 * x1))
        val y0 = 0.5 * sqrt((x0 * x0) + (x0 * x0))

        return (y0 + ((y1 - y0) * ((x - x0) / (x1 - x0)))).toFloat()
    }

    private fun calculateXPositionOffset(){
        if(currentDirection == Direction.HORIZONTAL){
            accumulatedScrollOffset.x -= distanceX
        }
    }

    private fun drawEventIndicatorCircle(canvas: Canvas, x: Float, y: Float, color: Int){
        dayPaint.color = color
        when(eventIndicatorStyle){
            IndicatorStyle.SMALL_INDICATOR -> {
                dayPaint.style = Paint.Style.FILL
                drawCircle(canvas, smallIndicatorRadius, x, y)
            }
            IndicatorStyle.NO_FILL_LARGE_INDICATOR ->{
                dayPaint.style =Paint.Style.STROKE
                drawDayCircleIndicator(IndicatorStyle.NO_FILL_LARGE_INDICATOR, canvas, x, y, color)
            }
            else ->{
                drawDayCircleIndicator(IndicatorStyle.FILL_LARGE_INDICATOR, canvas, x, y, color)
            }
        }
    }

    private fun drawDayCircleIndicator(indicatorStyle: IndicatorStyle, canvas: Canvas, x: Float, y: Float, color: Int){
        drawDayCircleIndicator(indicatorStyle, canvas, x, y, color, 1.0f)
    }

    private fun drawDayCircleIndicator(indicatorStyle: IndicatorStyle, canvas: Canvas, x: Float, y: Float, color: Int, circleScale: Float){
        val strokeWidth = dayPaint.strokeWidth

        if(indicatorStyle == IndicatorStyle.NO_FILL_LARGE_INDICATOR){
            dayPaint.strokeWidth = 2 * screenDensity
            dayPaint.style = Paint.Style.STROKE
        }else{
            dayPaint.style = Paint.Style.FILL
        }

        drawCircle(canvas, x, y, color, circleScale)
        dayPaint.strokeWidth = strokeWidth
        dayPaint.style = Paint.Style.FILL
    }

    // Draw Circle on certain days to highlight them
    private fun drawCircle(canvas: Canvas, x: Float, y: Float, color: Int, circleScale: Float){
        dayPaint.color = color
        if(animationStatus == ANIMATE_INDICATORS){
            val maxRadius = circleScale * bigCircleIndicatorRadius * 1.4f
            drawCircle(canvas, if (growFactorIndicator > maxRadius) maxRadius else growFactorIndicator, x,y - textHeight / 6)
        }else{
            drawCircle(canvas, circleScale * bigCircleIndicatorRadius, x, y - (textHeight / 6))
        }
    }

    private fun drawCircle(canvas: Canvas, radius: Float, x: Float, y: Float){
        canvas.drawCircle(x, y, radius, dayPaint)
    }

    /*
    *
    * dates methods
    * */

    // zero based indexes used internally so instead of returning range of 1-7 like calendar class
    // it returns 0-6 where 0 is Sunday instead of 1
    fun getDayOfWeek(calendar: LocalDate): Int {
        var dayOfWeek = calendar.dayOfWeek.value - firstDayOfWeekToDraw
        dayOfWeek = if(dayOfWeek < 0)  7 + dayOfWeek else dayOfWeek
        return dayOfWeek
    }

    private fun toMonthWithOffset(date: LocalDate, offset: Int): LocalDate{
        return date.plusMonths(offset.toLong()).withDayOfMonth(1)
    }

    fun getCurrentMonth(): LocalDate {
        return toMonthWithOffset(today, monthsScrolledSoFar())
    }

    fun getWeekNumberForCurrentMonth(): Int {
        return weekOfMonth(currentCalendar)
    }

    /*
    *  events methods
    * */
    fun addEvent(event: Event){
        eventsContainer.addEvent(event)
    }

    fun addEvents(events: List<Event>){
        eventsContainer.addEvents(events)
    }

    fun removeEvent(event: Event){
        eventsContainer.removeEvent(event)
    }

    fun removeEvents(events: List<Event>){
        eventsContainer.removeEvents(events)
    }

    fun removeEventsForDate(datetime: LocalDate){
        eventsContainer.removeEventsByDate(datetime)
    }

    fun removeAllEvents(){
        eventsContainer.removeAllEvents()
    }

    fun getEventsForDate(datetime: LocalDate): List<Event>{
        return eventsContainer.getEventsForDate(datetime)
    }

    fun getEventsForMonth(datetime: LocalDate): List<Event>{
        return eventsContainer.getEventsForMonth(datetime)
    }

    fun getEventsForMonthAndYear(month: Int, year: Int):List<Events>{
        return eventsContainer.getEventsForMonthAndYear(month, year)
    }
}