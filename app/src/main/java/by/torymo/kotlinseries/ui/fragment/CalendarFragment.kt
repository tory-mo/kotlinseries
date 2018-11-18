package by.torymo.kotlinseries.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import by.torymo.kotlinseries.R
import by.torymo.kotlinseries.Utility
import by.torymo.kotlinseries.data.SeriesRepository.Companion.EpisodeStatus
import by.torymo.kotlinseries.ui.adapters.EpisodesForDateAdapter
import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.network.MdbSearchResponse
import by.torymo.kotlinseries.data.network.Requester
import by.torymo.kotlinseries.ui.CalendarView
import by.torymo.kotlinseries.ui.model.EpisodeCalendarViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class CalendarFragment: Fragment(), CalendarView.EventHandler, EpisodesForDateAdapter.OnItemClickListener{

    private lateinit var viewModel: EpisodeCalendarViewModel
    private val requester = Requester()

    private val callback = object : Callback<MdbSearchResponse> {
        override fun onFailure(call: Call<MdbSearchResponse>?, t: Throwable?) {
            Log.e("MainActivity", "Problem calling Github API", t)
        }

        override fun onResponse(call: Call<MdbSearchResponse>?, response: Response<MdbSearchResponse>?) {
            response?.isSuccessful.let {
                val resultList = response?.body()
                var i = 0
                i++
            }
        }
    }

    override fun onDayPress(date: Long) {
        showEpisodesForDay(date)
    }

    override fun onMonthChanged(start: Long, end: Long) {
        getEpisodesForMonth(start, end)
    }

    override fun onItemClick(episode: Episode, item: View) {
        episode.id?.let {
            viewModel.changeEpisodeSeen(episode.id, if(episode.seen) EpisodeStatus.NOT_SEEN else EpisodeStatus.NOT_SEEN)
        }
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

        viewModel.episodeDates.observe(viewLifecycleOwner, Observer<List<Long>>{
            dates -> dates?.let{ calendar_view.updateCalendar(dates)}
        })

        viewModel.episodeList.observe(viewLifecycleOwner, Observer<List<Episode>>{
            episodes -> episodes?.let { populateEpisodes(episodes) }
        })

        requester.getAiringToday(callback)

    }

    private fun changeSeenTitle(menuItem: MenuItem?){

        val seenTitle = if(Utility().getSeenParam(activity)) R.string.action_all else R.string.action_only_seen
        menuItem?.title = resources.getString(seenTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.calendar_menu, menu)
        changeSeenTitle(menu?.findItem(R.id.action_only_seen))
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId){
        R.id.action_update_episodes -> consume{
            //viewModel.updateEpisodes()
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