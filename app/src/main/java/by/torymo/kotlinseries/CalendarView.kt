package by.torymo.kotlinseries

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.GridView
import android.util.AttributeSet
import android.view.View
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.util.*


class CalendarView : LinearLayout {

    private var tvPrevious: TextView? = null
    private var tvNext: TextView? = null
    private var tvMonth: TextView? = null
    private var gvCalendar: GridView? = null

    private var eventHandler: EventHandler? = null

    // current displayed month
    private var currentDate = DateTime.now(DateTimeZone.UTC).withMillisOfDay(0)

    val currentMonthStartEnd: Array<Long>
        get() {
            val calendar = currentDate.hourOfDay().setCopy(0)
                    .millisOfDay().setCopy(0)
                    .dayOfMonth().setCopy(1)
            val startDate = calendar.millis
            val endDate = calendar.dayOfMonth().setCopy(calendar.dayOfMonth().maximumValue).millis
            return arrayOf(startDate, endDate)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initControl(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initControl(context)
    }

    private fun initControl(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.calendar_view, this)

        tvPrevious = findViewById(R.id.tvPrevious)
        tvNext = findViewById(R.id.tvNext)
        tvMonth = findViewById(R.id.tvMonth)
        val gvHeader = findViewById<GridView>(R.id.gvHeader)
        gvCalendar = findViewById(R.id.gvCalendar)

        gvHeader.adapter = ArrayAdapter(context, R.layout.day_name, R.id.name_day, resources.getStringArray(R.array.weekDays))

        assignClickHandlers()
        updateCalendar()
    }




    private fun nextClick(){
        currentDate = currentDate.plusMonths(1)
        updateCalendar()
        if (eventHandler != null) {
            val startEnd = currentMonthStartEnd
            eventHandler!!.onMonthChanged(startEnd[0], startEnd[1])
        }
    }

    private fun previousClick(){
        currentDate = currentDate.minusMonths(1)
        updateCalendar()
        if (eventHandler != null) {
            val startEnd = currentMonthStartEnd
            eventHandler!!.onMonthChanged(startEnd[0], startEnd[1])
        }
    }

    private fun assignClickHandlers() {
        tvNext?.setOnClickListener{nextClick()}

        tvPrevious?.setOnClickListener{previousClick()}

        gvCalendar?.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // handle long-press
                if (eventHandler == null)
                    return

                val date = parent.getItemAtPosition(position) as Long
                eventHandler!!.onDayPress(date)
            }
        }
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
        val sdf = SimpleDateFormat(MONTH_TITLE_FORMAT)
        tvMonth?.text = sdf.format(currentDate.millis)
        if (eventHandler != null) {
            val now = DateTime.now(DateTimeZone.UTC).withMillisOfDay(0)
            eventHandler!!.onDayPress(now.millis)
        }
    }

    private inner class CalendarAdapter internal constructor(private val mContext: Context, days: MutableList<Long?>, // days with events
                                                             private val eventDays: List<Long>) : ArrayAdapter<Long>(mContext, R.layout.day, days) {

        // for view inflation
        private val inflater: LayoutInflater  = LayoutInflater.from(mContext)

        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            val view = v ?: inflater.inflate(R.layout.day, parent, false)

            val date = getItem(position)

            val dayView = view!!.findViewById<TextView>(R.id.date)
            val today = Calendar.getInstance()
            when{
                date == null ->{
                    dayView.isClickable = false
                    dayView.isFocusable = false
                    dayView.text = ""
                }
                eventDays.contains(date) ->{
                    if (today.timeInMillis == date) {
                        dayView.setTextColor(mContext.resources.getColor(R.color.light_bg))
                        dayView.setBackgroundResource(R.drawable.today_episode_day)
                    } else {
                        // mark this day for event
                        dayView.setBackgroundResource(R.drawable.episode_day)
                    }
                    val d = Calendar.getInstance()
                    d.timeInMillis = date
                    dayView.text = d.get(Calendar.DAY_OF_MONTH).toString()
                }
                else -> {
                    if (today.timeInMillis == date) {
                        dayView.setBackgroundResource(R.drawable.today_day)
                    } else {
                        dayView.isClickable = false
                        dayView.isFocusable = false
                    }
                    val d = Calendar.getInstance()
                    d.timeInMillis = date
                    dayView.text = d.get(Calendar.DAY_OF_MONTH).toString()
                }
            }

            return view
        }
    }

    fun setEventHandler(eventHandler: EventHandler) {
        this.eventHandler = eventHandler
    }

    interface EventHandler {
        fun onDayPress(date: Long)
        fun onMonthChanged(start: Long, end: Long)
    }

    companion object {

        private const val MONTH_TITLE_FORMAT = "MMMM yyyy"

        internal const val FIRST_DAY_OF_WEEK = 1 // Sunday = 0, Monday = 1
    }

}