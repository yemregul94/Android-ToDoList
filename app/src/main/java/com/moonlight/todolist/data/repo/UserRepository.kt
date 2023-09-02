package com.moonlight.todolist.data.repo

import androidx.lifecycle.MutableLiveData
import com.moonlight.todolist.data.datasource.UserDataSource
import com.moonlight.todolist.data.model.UserData

class UserRepository(var uds: UserDataSource) {

    fun updateUserData(user: UserData, uid: String?) = uds.updateUserData(user, uid)

    fun getUserData(uid: String?) : MutableLiveData<UserData?> = uds.getUserData(uid)

    suspend fun deleteUserData(uid: String?) = uds.deleteUserData(uid)

    fun getTheme() : String? = uds.getTheme()

    fun saveTheme(mode: String) = uds.saveTheme(mode)

    fun getSortIndex() : String? = uds.getSortIndex()

    fun saveSortIndex(sortIndex: String) = uds.saveSortIndex(sortIndex)
}