package com.moonlight.todolist.data.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.moonlight.todolist.data.datasource.ToDoDataSource
import com.moonlight.todolist.data.model.ToDoListItem

class ToDoRepository(var tds: ToDoDataSource) {

    fun getListItems(uid: String?) : MutableLiveData<List<ToDoListItem>> = tds.getListItems(uid)

    fun newListItem(list: ToDoListItem, uid: String?) = tds.newListItem(list, uid)

    fun updateListItem(list: ToDoListItem, uid: String?) = tds.updateListItem(list, uid)

    fun deleteListItem(listID: String, uid: String?) = tds.deleteListItem(listID, uid)

    fun getAlarms(context: Context, uid: String?) = tds.getAlarms(context ,uid)
}