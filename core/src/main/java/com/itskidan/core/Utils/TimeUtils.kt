package com.itskidan.core.Utils

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TimeUtils {
    companion object {
        fun getFormattedCurrentTime(timeMillis: Long): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val instant = java.time.Instant.ofEpochMilli(timeMillis)
                val localDateTime =
                    java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
                val formatter = java.time.format.DateTimeFormatter.ofPattern(
                    "HH:mm, MMM d, yyyy",
                    Locale.getDefault()
                )
                localDateTime.format(formatter)
            } else {
                val date = Date(timeMillis)
                val simpleDateFormat = SimpleDateFormat("HH:mm, MMM d, yyyy", Locale.getDefault())
                simpleDateFormat.timeZone = TimeZone.getDefault()
                simpleDateFormat.format(date)
            }
        }
    }
}