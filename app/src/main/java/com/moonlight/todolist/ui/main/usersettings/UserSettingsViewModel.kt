package com.moonlight.todolist.ui.main.usersettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moonlight.todolist.R
import com.moonlight.todolist.data.repo.AuthRepository
import com.moonlight.todolist.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(var arepo: AuthRepository, var urepo: UserRepository) : ViewModel() {

    val errorMessage: LiveData<String> get() = _errorMessage
    private var _errorMessage: MutableLiveData<String> = MutableLiveData()

    val successMessage: LiveData<Int> get() = _successMessage
    private var _successMessage: MutableLiveData<Int> = MutableLiveData()

    suspend fun saveAnonToEmail(email: String, password: String) : Boolean{
        try {
            arepo.saveAnonToEmail(email, password)
            sendSuccess(R.string.save_guest_success)
            return true
        }catch (e: Exception){
            e.localizedMessage?.let { sendError(it) }
            return false
        }
    }

    suspend fun updateEmail(email: String){
        try {
            arepo.updateEmail(email)
            sendSuccess(R.string.change_email_success)
        }catch (e: Exception){
            e.localizedMessage?.toString()?.let { sendError(it) }
        }
    }

    suspend fun updatePassword(password: String){
        try {
            arepo.updatePassword(password)
            sendSuccess(R.string.change_password_success)
        }catch (e: Exception){
            e.localizedMessage?.toString()?.let { sendError(it) }
        }
    }

    suspend fun deleteUser(){
        try {
            arepo.deleteUser()
            sendSuccess(R.string.delete_user_success)
        }catch (e: Exception){
            e.localizedMessage?.toString()?.let { sendError(it) }
        }
    }

    suspend fun deleteUserData(uid: String){
        try {
            urepo.deleteUserData(uid)
            sendSuccess(R.string.delete_user_data_success)
        }catch (e: Exception){
            e.localizedMessage?.toString()?.let { sendError(it) }
        }
    }

    fun sendError(errorMessage: String){
        _errorMessage.value = errorMessage
    }

    private fun sendSuccess(successMessage: Int){
        _successMessage.value = successMessage
    }

}