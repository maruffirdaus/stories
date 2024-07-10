package dev.maruffirdaus.stories.helper

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateHelper {
    fun covertDate(dateTime: String): String {
        val odt = OffsetDateTime.parse(dateTime)
        val dtf = DateTimeFormatter.ofPattern("HH:mm · d MMMM yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        return dtf.format(odt)
    }
}