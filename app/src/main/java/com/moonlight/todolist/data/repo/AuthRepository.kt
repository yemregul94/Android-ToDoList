package com.moonlight.todolist.data.repo

import com.google.firebase.auth.FirebaseUser
import com.moonlight.todolist.data.datasource.AuthDataSource

class AuthRepository(var ads: AuthDataSource) {

    fun checkFirstLaunch() : Boolean = ads.checkFirstLaunch()

    fun updateFirstLaunch() = ads.updateFirstLaunch()

    fun getUser(): FirebaseUser? = ads.getUser()

    suspend fun login(email: String, password: String) = ads.login(email, password)

    suspend fun register(email: String, password: String) = ads.register(email, password)

    suspend fun resetPassword(email: String) = ads.resetPassword(email)

    suspend fun loginAsAnon() = ads.loginAsAnon()

    suspend fun saveAnonToEmail(email: String, password: String) = ads.saveAnonToEmail(email, password)

    suspend fun updateEmail(email: String) = ads.updateEmail(email)

    suspend fun updatePassword(password: String) = ads.updatePassword(password)

    fun getLastLogin() : String? = ads.getLastLogin()

    fun checkNewUser() : Boolean = ads.checkNewUser()

    suspend fun deleteUser() = ads.deleteUser()

    fun signOut() = ads.signOut()
}