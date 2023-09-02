package com.moonlight.todolist.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moonlight.todolist.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(var arepo: AuthRepository) : ViewModel() {

    val errorMessage: LiveData<String> get() = _errorMessage
    private var _errorMessage: MutableLiveData<String> = MutableLiveData()

    suspend fun register(email: String, password: String){
        try {
            arepo.register(email, password)
        }catch (e: Exception){
            e.localizedMessage?.toString()?.let { sendError(it) }
        }
    }

    fun sendError(errorMessage: String){
        _errorMessage.value = errorMessage
    }
}