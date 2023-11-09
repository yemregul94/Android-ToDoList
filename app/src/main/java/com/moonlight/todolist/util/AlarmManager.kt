package com.moonlight.todolist.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.moonlight.todolist.data.model.ToDoListItem

fun setAlarm(context: Context, listItem: ToDoListItem) {

    val time = getTimeInMillis(listItem.alarmTime)
    cancelAlarm(context, listItem)

    val broadcastIntent = Intent(context, AlarmReceiver::class.java)
    broadcastIntent.putExtra("listItem", listItem)

    val pendingIntent = PendingIntent.getBroadcast(context, listItem.id.hashCode(), broadcastIntent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (System.currentTimeMillis() < time) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }
}

fun cancelAlarm(context: Context, listItem: ToDoListItem){

    val broadcastIntent = Intent(context, AlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(context, listItem.id.hashCode(), broadcastIntent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.cancel(pendingIntent)
    pendingIntent.cancel()
}