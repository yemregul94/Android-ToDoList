package com.moonlight.todolist.util

import android.Manifest
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.NavDeepLinkBuilder
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.ui.main.MainActivity


class AlarmReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val item = intent?.getSerializableExtra("listItem") as? ToDoListItem

        val args = Bundle()
        args.putSerializable("toDoListItem", item)

        val pendingIntent = NavDeepLinkBuilder(context!!)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_main)
            .setDestination(R.id.listDetailsFragment)
            .setArguments(args)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context, "todolist")
            .setSmallIcon(R.drawable.ic_list)
            .setColor(getColor(context, R.color.theme_primary))
            .setContentTitle(context.getString(R.string.reminder))
            .setContentText(item?.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager = context.let { NotificationManagerCompat.from(it) }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, context.getString(R.string.no_notification_permission), Toast.LENGTH_LONG).show()
            return
        }

        notificationManager!!.notify(item?.id.hashCode(), builder.build())

    }
}