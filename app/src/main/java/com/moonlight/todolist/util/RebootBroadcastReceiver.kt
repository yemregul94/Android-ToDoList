package com.moonlight.todolist.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moonlight.todolist.data.repo.AuthRepository
import com.moonlight.todolist.data.repo.ToDoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RebootBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var trepo: ToDoRepository
    @Inject lateinit var arepo: AuthRepository

    override fun onReceive(context: Context?, intent: Intent?) {

        trepo.getAlarms(context!!, arepo.getUser()?.uid)
    }
}