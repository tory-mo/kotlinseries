package by.torymo.kotlinseries.ui

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.util.AttributeSet
import android.view.View
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.util.*
import by.torymo.kotlinseries.R
import kotlinx.android.synthetic.main.calendar_view.view.*


class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr){

    private var eventHandler: EventHandler? = null

    // current displayed month
    private var currentDate = DateTime.now(DateTimeZone.UTC).withMillisOfDay(0)

    init {
        initControl(context)
    }

    companion object {
        private const val MONTH_TITLE_FORMAT = "MMMM yyyy"
        internal const val FIRST_DAY_OF_WEEK = 1 // Sunday = 0, Monday = 1
    }

    val currentMonthStartEnd: Array<Long>
        get() {
            val calendar = currentDate.hourOfDay().setCopy(0)
                    .millisOfDay().setCopy(0)
                    .dayOfMonth().setCopy(1)
            val startDate = calendar.millis
            val endDate = calendar.dayOfMonth().setCopy(calendar.dayOfMonth().maximumValue).millis
            return arrayOf(startDate, endDate)
        }


    private fun initControl(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.calendar_view, this)

        gvHeader.adapter = ArrayAdapter(context, R.layout.day_name, R.id.name_day, resources.getStringArray(R.array.weekDays))
        tvNext?.setOnClickListener{changeMonth(1)}
        tvPrevious?.setOnClickListener{changeMonth(-1)}
        gvCalendar?.setOnItemClickListener {parent, _, position, _ -> calendarItemClick(parent, position)}

        updateCalendar()
    }


    private fun changeMonth(to: Int){
        currentDate = when{
            to > 0 -> currentDate.plusMonths(1)
            else -> currentDate.minusMonths(1)
        }

        updateCalendar()
        if (eventHandler != null) {
            val startEnd = currentMonthStartEnd
            eventHandler!!.onMonthChanged(startEnd[0], startEnd[1])
        }
    }

    private fun calendarItemClick(parent: AdapterView<*>, position: Int){
        val date = parent.getItemAtPosition(position) as Long?
        if(date != null)
            eventHandler?.onDayPress(date)
    }

    @JvmOverloads
    fun updateCalendar(events: List<Long> = listOf()) {
        val cells = mutableListOf<Long?>()
        var calendar = currentDate.hourOfDay().setCopy(0)
                .millisOfDay().setCopy(0)
                .dayOfMonth().setCopy(1)
        var firstDay = calendar.dayOfWeek().get() - FIRST_DAY_OF_WEEK //convert to start with zero
        if (firstDay < 0) {
            firstDay += 7
        }
        val lastDay = calendar.dayOfMonth().maximumValue


        for (i in 0 until firstDay) {
            cells.add(null)
        }

        for (i in firstDay until lastDay + firstDay) {
            cells.add(calendar.millis)
            calendar = calendar.plusDays(1)
        }

        // update grid
        gvCalendar?.adapter = CalendarAdapter(context, cells, events)

        // update title
        val sdf = SimpleDateFormat(MONTH_TITLE_FORMAT, Locale.UK)
        tvMonth?.text = sdf.format(currentDate.millis)

        val now = DateTime.now(DateTimeZone.UTC).withMillisOfDay(0)
        eventHandler?.onDayPress(now.millis)
    }

    private inner class CalendarAdapter internal constructor(mContext: Context, days: MutableList<Long?>, // days with events
                                                             private val eventDays: List<Long>) : ArrayAdapter<Long>(mContext, R.layout.day, days) {

        // for view inflation
        private val inflater: LayoutInflater  = LayoutInflater.from(mContext)

        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            val view = v ?: inflater.inflate(R.layout.day, parent, false)

            val date = getItem(position)
            val dayView = view!!.findViewById<TextView>(R.id.date)
            val today = DateTime.now(DateTimeZone.UTC)
            val dayLabel = today.withMillis(date ?: today.millis).dayOfMonth.toString()

            when{
                date == null ->{
                    dayView.notResponsive()
                }
                eventDays.contains(date) ->{
                    if (today.millis == date) {
                        dayView.todayWithEvent(dayLabel)
                    } else {
                        dayView.eventDay(dayLabel)
                    }
                }
                else -> {
                    if (today.millis == date) {
                        dayView.today(dayLabel)
                    } else {
                        dayView.notResponsive(dayLabel)
                    }
                }
            }

            return view
        }

        private fun TextView.notResponsive(txt: String = ""){
            isClickable = false
            isFocusable = false
            text = txt
        }

        private fun TextView.todayWithEvent(txt: String){
            setTextColor(resources.getColor(R.color.light_bg))
            setBackgroundResource(R.drawable.today_episode_day)
            text = txt
        }

        private fun TextView.eventDay(txt: String){
            setBackgroundResource(R.drawable.episode_day)
            text = txt
        }

        private fun TextView.today(txt: String = ""){
            setBackgroundResource(R.drawable.today_day)
            text = txt
        }
    }

    fun setEventHandler(eventHandler: EventHandler) {
        this.eventHandler = eventHandler
    }

    interface EventHandler {
        fun onDayPress(date: Long)
        fun onMonthChanged(start: Long, end: Long)
    }
}