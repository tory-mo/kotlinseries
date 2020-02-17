package by.torymo.kotlinseries

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

object DateTimeUtils {

    private const val dateStrFormat = "dd MMMM yyyy"

    fun now(): LocalDate{
        return LocalDate.now()
    }

    fun nowInMillis(): Long{
        return toMilliseconds(now())
    }

    fun toMilliseconds(date: LocalDate): Long{
        return date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    fun toMilliseconds(date: String, format: String): Long{
        return  toMilliseconds(LocalDate.parse(date, DateTimeFormatter.ofPattern(format)))
    }

    fun toLocalDateTime(milliseconds: Long): LocalDateTime{
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun format(date: Long, format: String = dateStrFormat): String{
        return toLocalDateTime(date).format(DateTimeFormatter.ofPattern(format))
    }

    fun timezone(): String{
        return ZonedDateTime.now().zone.id
    }
}
