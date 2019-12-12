package by.torymo.kotlinseries.calendar

import org.threeten.bp.LocalDateTime

data class Event(val color: Int, val timeInMillis: LocalDateTime, val data: Any? = null)