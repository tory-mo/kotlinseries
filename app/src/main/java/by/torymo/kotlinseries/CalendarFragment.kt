package by.torymo.kotlinseries

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import by.torymo.kotlinseries.adapters.EpisodesForDateAdapter
import by.torymo.kotlinseries.domain.Episode
import by.torymo.kotlinseries.ui.EpisodeCalendarViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


class CalendarFragment: Fragment(),  CalendarView.EventHandler, EpisodesForDateAdapter.OnItemClickListener{

    private lateinit var viewModel: EpisodeCalendarViewModel

    override fun onDayPress(date: Long) {
        showEpisodesForDay(date)
    }

    override fun onMonthChanged(start: Long, end: Long) {
        getEpisodesForMonth(start, end)
    }

    override fun onItemClick(episode: Episode, item: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(EpisodeCalendarViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar_view.setEventHandler(this)
        val startend = calendar_view.currentMonthStartEnd
        getEpisodesForMonth(startend[0], startend[1])

        viewModel.episodeDates.observe(this, Observer<List<Long>>{
            dates -> dates?.let{ calendar_view.updateCalendar(dates)}
        })

        viewModel.episodeList.observe(this, Observer<List<Episode>>{
            episodes -> episodes?.let { populateEpisodes(episodes) }
        })
    }

    private fun changeSeenTitle(menuItem: MenuItem?){

        val seenTitle = if(Utility().getSeenParam(activity))R.string.action_all else R.string.action_only_seen
        menuItem?.title = resources.getString(seenTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.calendar_menu, menu)
        changeSeenTitle(menu?.findItem(R.id.action_only_seen))
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId){
        R.id.action_update_episodes -> consume{

        }
        R.id.action_only_seen -> consume{
            Utility().changeSeenParam(activity)
            changeSeenTitle(item)
            val startend = calendar_view.currentMonthStartEnd
            getEpisodesForMonth(startend[0], startend[1])
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showEpisodesForDay(date: Long){
        val today = DateTime.now(DateTimeZone.UTC).withMillisOfDay(0)
        if (today.millis == date) {
            tvToday.text = resources.getString(R.string.today)
        } else {
            tvToday.text = Utility().dateToStrFormat.format(date)
        }
        viewModel.getEpisodesForDate(date)
    }

    private fun getEpisodesForMonth(start: Long, end: Long){
        viewModel.getEpisodeDatesBetweenDates(start, end)
    }

    private fun populateEpisodes(episodeList: List<Episode>){
        lvEpisodesForDay.adapter = EpisodesForDateAdapter(episodeList, this)
    }

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
}