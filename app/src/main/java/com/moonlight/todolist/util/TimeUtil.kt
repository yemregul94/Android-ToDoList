package com.moonlight.todolist.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatTime(time: String): String {
    val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    val date = inputFormat.parse(time)

    val outputFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault())

    return outputFormat.format(date!!)
}

fun getTimeInMillis(time: String): Long {
    val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    val cal = Calendar.getInstance()
    cal.time = inputFormat.parse(time)!!
    cal.set(Calendar.SECOND, 0)

    return cal.timeInMillis
}