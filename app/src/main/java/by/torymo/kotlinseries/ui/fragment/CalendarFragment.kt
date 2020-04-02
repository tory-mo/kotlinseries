package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.calendar.CalendarView
import by.torymo.kotlinseries.calendar.Event
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.ui.MainActivity
import by.torymo.kotlinseries.ui.adapters.EpisodesForDateAdapter
import by.torymo.kotlinseries.ui.model.CalendarViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*

import org.threeten.bp.LocalDate
import androidx.recyclerview.widget.DividerItemDecoration

class CalendarFragment: Fragment(), EpisodesForDateAdapter.OnItemClickListener,
        CalendarView.CalendarViewListener,
        CalendarViewModel.EpisodesCallback,
        MainActivity.UpdateListener{

    private var displayedMonth = DateTimeUtils.now()
    private var dateClicked = DateTimeUtils.now()
    private val currentYear = displayedMonth.year
    private var episodesAdapter: EpisodesForDateAdapter? = null

    private lateinit var viewModel: CalendarViewModel

    override fun onDayClick(dateClicked: LocalDate) {
        this.dateClicked = dateClicked
        showEpisodesForDay(DateTimeUtils.toMilliseconds(dateClicked))
    }

    override fun onEpisodesBetweenDatesComplete(dates: List<Long>) {
        val events = mutableListOf<Event>()
        val color = resources.getColor(R.color.colorAccent)
        dates.forEach{
            events.add(Event(color, DateTimeUtils.toLocalDateTime(it)))
        }

        compactcalendar_view?.removeAllEvents()
        compactcalendar_view?.addEvents(events)
    }

    override fun onEpisodesForDateComplete(episodes: List<ExtendedEpisode>) {
        populateEpisodes(episodes)
    }

    override fun onMonthScroll(displayedMonth: LocalDate) {
        this.displayedMonth = displayedMonth

        toolbarTitle.text = calendarTitle(displayedMonth)
        getEpisodesForMonth(displayedMonth)
    }

    override fun onItemClick(episode: ExtendedEpisode, item: View) {
        episode.id?.let {
            viewModel.changeEpisodeSeen(it, !episode.seen)
            viewModel.getEpisodesForDate(episode.date)
        }
    }

    override fun onUpdated() {
        getEpisodesForMonth(displayedMonth)
        showEpisodesForDay(DateTimeUtils.toMilliseconds(dateClicked))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            (it as MainActivity).setListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        viewModel.setEpisodeListCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarTitle.text = calendarTitle(displayedMonth)
        rlToolbarTitle.setOnClickListener {
            if(compactcalendar_view.visibility == View.VISIBLE) {
                compactcalendar_view.visibility = View.GONE
                ivCalendarExpandedIcon.setImageResource(R.drawable.menu_down)
            }
            else {
                compactcalendar_view.visibility = View.VISIBLE
                ivCalendarExpandedIcon.setImageResource(R.drawable.menu_up)
            }
        }

        episodesAdapter = EpisodesForDateAdapter(this)
        lvEpisodesForDay.adapter = episodesAdapter

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        lvEpisodesForDay.addItemDecoration(decoration)

        compactcalendar_view.setListener(this)
        getEpisodesForMonth(displayedMonth)
        showEpisodesForDay(DateTimeUtils.toMilliseconds(dateClicked))
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(calendarToolbar)

    }

    private fun changeSeenTitle(menuItem: MenuItem?){
        val seenTitle = if(viewModel.getSeenParam()) R.string.action_all else R.string.action_only_seen
        menuItem?.title = resources.getString(seenTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.calendar_menu, menu)
        changeSeenTitle(menu.findItem(R.id.action_only_seen))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.action_only_seen -> {
            viewModel.changeSeenParam()
            changeSeenTitle(item)
            getEpisodesForMonth(compactcalendar_view.getCurrentMonth())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showEpisodesForDay(date: Long){
        val today = DateTimeUtils.nowInMillis()
        if (today == date) {
            tvToday?.text = resources.getString(R.string.today)
        } else {
            tvToday?.text = DateTimeUtils.format(date)
        }
        viewModel.getEpisodesForDate(date)
    }

    private fun getEpisodesForMonth(date: LocalDate){
        viewModel.getEpisodeDatesBetweenDates(DateTimeUtils.toMilliseconds(date.withDayOfMonth(1)),
                DateTimeUtils.toMilliseconds(date.withDayOfMonth(date.lengthOfMonth())))
    }

    private fun populateEpisodes(episodeList: List<ExtendedEpisode>){
        episodesAdapter?.updateItems(episodeList)
    }

    private fun calendarTitle(date: LocalDate): String{
        return getString(R.string.format_month_year, date.month.name.toLowerCase().capitalize(), if (date.year != currentYear) date.year else "")
    }
}