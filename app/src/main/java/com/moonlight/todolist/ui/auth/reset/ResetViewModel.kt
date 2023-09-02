package com.moonlight.todolist.ui.auth.reset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moonlight.todolist.R
import com.moonlight.todolist.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetViewModel @Inject constructor(var arepo: AuthRepository) : ViewModel() {

    val errorMessage: LiveData<String> get() = _errorMessage
    private var _errorMessage: MutableLiveData<String> = MutableLiveData()

    val successMessage: LiveData<Int> get() = _successMessage
    private var _successMessage: MutableLiveData<Int> = MutableLiveData()

    suspend fun resetPassword(email: String){
        try {
            arepo.resetPassword(email)
            sendSuccess(R.string.password_reset_success)
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