package by.torymo.kotlinseries.calendar

import org.threeten.bp.LocalDate

class EventsContainer {

    private val eventsByMonthAndYearMap = mutableMapOf<String, MutableList<Events>>()

    fun addEvent(event: Event){
        val key = getKeyForCalendarEvent(event.timeInMillis.toLocalDate())

        var eventsForMonth = eventsByMonthAndYearMap[key]
        if(eventsForMonth == null) eventsForMonth = mutableListOf()

        val eventsForTargetDay = eventsForMonth.find { it.timeInMillis == event.timeInMillis.toLocalDate() }
        if(eventsForTargetDay == null){
            eventsForMonth.add(Events(event.timeInMillis.toLocalDate(), mutableListOf(event)))
        }else{
            eventsForTargetDay.events.add(event)
            // TODO: check this
        }
        eventsByMonthAndYearMap[key] = eventsForMonth
    }

    fun addEvents(events: List<Event>){
        for(element in events){
            addEvent(element)
        }
    }

    fun removeEvent(event: Event){
        val key = getKeyForCalendarEvent(event.timeInMillis.toLocalDate())

        eventsByMonthAndYearMap[key]?.let {
            it.forEach{evt-> evt.removeEvent(event)}
            it.removeAll { evt-> evt.isEmpty() }
        }

        if(eventsByMonthAndYearMap[key] != null){
            if(eventsByMonthAndYearMap[key]!!.isEmpty()) eventsByMonthAndYearMap.remove(key)
        }
    }

    fun removeEvents(events: List<Event>){
        for(element in events){
            removeEvent(element)
        }
    }

    fun removeAllEvents(){
        eventsByMonthAndYearMap.clear()

    }

    fun removeEventsByDate(datetime: LocalDate){
        val key = getKeyForCalendarEvent(datetime)
        eventsByMonthAndYearMap[key]?.let {
            it.removeAll {evts->
                evts.timeInMillis == datetime
            }
        }

        if(eventsByMonthAndYearMap[key] != null){
            if(eventsByMonthAndYearMap[key]!!.isEmpty()) eventsByMonthAndYearMap.remove(key)
        }
    }

    fun getEventsForDate(datetime: LocalDate):List<Event>{
        val key = getKeyForCalendarEvent(datetime)
        return eventsByMonthAndYearMap[key]?.find { it.timeInMillis == datetime }?.events.orEmpty()
    }

    fun getEventsForMonthAndYear(month: Int, year: Int):List<Events>{
        return eventsByMonthAndYearMap[year.toString() + "_" + month].orEmpty()
    }

    fun getEventsForMonth(datetime: LocalDate): List<Event>{
        val key = getKeyForCalendarEvent(datetime)
        return eventsByMonthAndYearMap[key]?.flatMap { it.events}.orEmpty()
    }

    //E.g. 4 2016 becomes 2016_4
    private fun getKeyForCalendarEvent(datetime: LocalDate): String{
        return datetime.year.toString() + "_" + datetime.monthValue
    }
}