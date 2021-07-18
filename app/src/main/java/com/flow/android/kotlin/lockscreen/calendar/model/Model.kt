package com.flow.android.kotlin.lockscreen.calendar.model

import androidx.annotation.ColorInt
import com.flow.android.kotlin.lockscreen.util.BLANK

sealed class Model {
    open class Calendar(
            val id: Long,
            val name: String
    ) : Model()

    class PermissionDenied : Calendar(0, BLANK)

    data class CalendarEvent (
            val begin: Long,
            @ColorInt
            val calendarColor: Int,
            val calendarDisplayName: String,
            val calendarId: Long,
            val end: Long,
            val eventId: Long,
            val id: Long,
            val title: String
    ) : Model()
}