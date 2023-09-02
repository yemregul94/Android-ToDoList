package com.moonlight.todolist.ui.main.listitemdetails

import androidx.lifecycle.ViewModel
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.repo.ToDoRepository
import com.moonlight.todolist.util.DATE_FORMAT
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel

class ListItemDetailsViewModel @Inject constructor(var trepo: ToDoRepository) : ViewModel() {

    fun newListItem(listItem: ToDoListItem, uid: String?){
        trepo.newListItem(listItem, uid)
    }

    fun updateListItem(listItem: ToDoListItem, uid: String?){
        trepo.updateListItem(listItem, uid)
    }

    fun formatTime(time: String): String {
        val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        val date = inputFormat.parse(time)

        val outputFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault())

        return outputFormat.format(date!!)
    }

}