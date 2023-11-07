package com.moonlight.todolist.ui.main.alarmList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.repo.ToDoRepository
import com.moonlight.todolist.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmListViewModel @Inject constructor(private var trepo: ToDoRepository, private var urepo: UserRepository) : ViewModel() {

    private var uid = ""

    val toDoListsItem: LiveData<List<ToDoListItem>> get() = _toDoListsItem
    private var _toDoListsItem: MutableLiveData<List<ToDoListItem>> = MutableLiveData()

    fun setUID(uid: String){
        this.uid = uid
        loadListItems()
    }

    private fun loadListItems(){
        _toDoListsItem = trepo.getListItems(uid)
    }

}