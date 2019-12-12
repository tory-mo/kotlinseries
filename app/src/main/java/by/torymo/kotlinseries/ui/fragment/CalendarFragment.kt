package by.torymo.kotlinseries.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import by.torymo.kotlinseries.DateTimeUtils
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.calendar.Event
import by.torymo.kotlinseries.data.db.ExtendedEpisode
import by.torymo.kotlinseries.ui.adapters.EpisodesForDateAdapter
import by.torymo.kotlinseries.ui.model.CalendarViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*

import org.threeten.bp.LocalDate

class CalendarFragment: Fragment(), EpisodesForDateAdapter.OnItemClickListener, by.torymo.kotlinseries.calendar.CalendarView.CalendarViewListener, CalendarViewModel.EpisodesCallback{

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

    override fun onEpisodesUpdated() {
        getEpisodesForMonth(displayedMonth)
        showEpisodesForDay(DateTimeUtils.toMilliseconds(dateClicked))
    }

    override fun onMonthScroll(displayedMonth: LocalDate) {
        toolbarTitle.text = getString(R.string.format_month_year, displayedMonth.month.name, if (displayedMonth.year != currentYear) displayedMonth.year else "")
        this.displayedMonth = displayedMonth

        getEpisodesForMonth(displayedMonth)
    }

    override fun onItemClick(episode: ExtendedEpisode, item: View) {
        episode.id?.let {
            viewModel.changeEpisodeSeen(it, !episode.seen)
            viewModel.getEpisodesForDate(episode.date)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.setEpisodeListCallback(this)
        viewModel.updateEpisodes()
        viewModel.requestAiringToday()
        episodesAdapter = EpisodesForDateAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle.text = DateTimeUtils.now().month.name

        compactcalendar_view.invalidate()
        compactcalendar_view.setListener(this)

        getEpisodesForMonth(compactcalendar_view.getCurrentMonth())

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

        lvEpisodesForDay.adapter = episodesAdapter
    }

    private fun changeSeenTitle(menuItem: MenuItem?){

        val seenTitle = if(viewModel.getSeenParam()) R.string.action_all else R.string.action_only_seen
        menuItem?.title = resources.getString(seenTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.calendar_menu, menu)
        changeSeenTitle(menu.findItem(R.id.action_only_seen))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.action_update_episodes -> consume{
            //viewModel.updateEpisodes()
        }
        R.id.action_only_seen -> consume{
            viewModel.changeSeenParam()
            changeSeenTitle(item)
            //getEpisodesForMonth(compactcalendar_view.getCurrentMonth())
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
        Log.d("show 123456", episodeList.size.toString())
    }

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
}