package com.flow.android.kotlin.lockscreen.util

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateString(simpleDateFormat: SimpleDateFormat): String {
    return simpleDateFormat.format(Date(this))
}

fun <T> List<T>.diff(list: List<T>) = Diff(
        first = list.filterNot { this.contains(it) },
        second = this.filterNot { list.contains(it) }
)

data class Diff<T>(
        val first: List<T>,
        val second: List<T>
)

val Float.toDp get() = this / Resources.getSystem().displayMetrics.density
val Float.toPx get() = this * Resources.getSystem().displayMetrics.density

val Int.toDp get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()