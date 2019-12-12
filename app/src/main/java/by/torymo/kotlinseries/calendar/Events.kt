package by.torymo.kotlinseries.calendar

import org.threeten.bp.LocalDate


data class Events(val timeInMillis: LocalDate, val events: MutableList<Event>){

    fun removeEvent(event: Event){
        events.removeAll { it == event }
    }

    fun isEmpty(): Boolean{
        return events.isEmpty()
    }
}