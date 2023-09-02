package com.moonlight.todolist.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.model.UserData
import com.moonlight.todolist.data.repo.ToDoRepository
import com.moonlight.todolist.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(var trepo: ToDoRepository, var urepo: UserRepository): ViewModel() {

    val user: LiveData<UserData?> get() = _user
    private var _user: MutableLiveData<UserData?> = MutableLiveData()

    val theme: LiveData<String> get() = _theme
    private var _theme: MutableLiveData<String> = MutableLiveData()

    init {
        getTheme()
    }

    fun createUserData(user: UserData, uid: String?){
        urepo.updateUserData(user, uid)
    }

    fun getUser(uid: String?){
        _user = urepo.getUserData(uid)
    }

    fun createFirstListItem(listItem:ToDoListItem, uid: String?){
        trepo.newListItem(listItem, uid)
    }

    fun saveTheme(mode: String){
        urepo.saveTheme(mode)
        getTheme()
    }

    fun getTheme(): String? {
        _theme.value = urepo.getTheme()
        return urepo.getTheme()
    }
}