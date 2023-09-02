package com.moonlight.todolist.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.moonlight.todolist.data.repo.AuthRepository
import com.moonlight.todolist.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(var arepo: AuthRepository, var urepo: UserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    var latestUser: FirebaseUser? = null

    val theme: LiveData<String> get() = _theme
    private var _theme: MutableLiveData<String> = MutableLiveData()

    var uid = currentUser.value?.uid

    init {
        getUser()
        getTheme()
    }

    fun checkFirstLaunch() : Boolean{
        return arepo.checkFirstLaunch()
    }

    fun updateFirstLaunch(){
        arepo.updateFirstLaunch()
    }

    fun getUser(){
        _currentUser.value = arepo.getUser()
        getUid()
    }

    private fun getUid(){
        uid = currentUser.value?.uid
    }

    fun getLastLogin() : String?{
        return arepo.getLastLogin()
    }

    fun checkNewUser() : Boolean {
        return arepo.checkNewUser()
    }

    private fun getTheme(): String? {
        _theme.value = urepo.getTheme()
        return urepo.getTheme()
    }

    fun signOut(){
        arepo.signOut()
        getUser()
    }
}