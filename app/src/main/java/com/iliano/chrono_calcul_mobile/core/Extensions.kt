package com.iliano.chrono_calcul_mobile.core

import android.content.Context
import android.text.format.DateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

fun Duration.toFormattedString(): String {
    val totalSeconds = inWholeSeconds
    val sign = if (totalSeconds < 0) {
        "-"
    } else {
        ""
    }

    val absoluteSeconds = kotlin.math.abs(totalSeconds)

    val hours = absoluteSeconds / 3600
    val minutes = (absoluteSeconds % 3600) / 60
    val seconds = absoluteSeconds % 60

    return Constants.Strings.DURATION_FORMAT.format(
        sign,
        hours,
        minutes,
        seconds
    )
}

fun LocalTime.toFormattedString(
    context: Context
): String {
    val pattern = if (DateFormat.is24HourFormat(context)) {
        "HH:mm"
    } else {
        "h:mm a"
    }

    return format(
        DateTimeFormatter.ofPattern(pattern)
    )
}


fun LocalTime.durationBetween(other: LocalTime): Duration {
    return java.time.Duration.between(this, other).toKotlinDuration()
}