package com.moonlight.todolist.data.datasource

import android.content.SharedPreferences
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSource @Inject constructor(var sp: SharedPreferences){

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user = auth.currentUser
    private var isNewUser = false

    init {
        auth.useAppLanguage()
    }

    fun checkFirstLaunch() : Boolean{
        return sp.getBoolean("firstLaunch", true)
    }

    fun updateFirstLaunch(){
        sp.edit().putBoolean("firstLaunch", false).apply()
    }

    fun getUser() : FirebaseUser? {
        user = auth.currentUser
        return user
    }

    suspend fun login(email:String, password:String){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveLastLogin()
                isNewUser = task.result.additionalUserInfo?.isNewUser!!
            }
        }.addOnFailureListener {

        }.await()

    }

    suspend fun register(email: String, password: String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                saveLastLogin()
                isNewUser = task.result.additionalUserInfo?.isNewUser!!
            }
        }.addOnFailureListener {

        }.await()
    }

    suspend fun resetPassword(email: String){
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful){

            }
        }.addOnFailureListener {

        }.await()
    }

    suspend fun loginAsAnon(){
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isNewUser = task.result.additionalUserInfo?.isNewUser!!
                }
            }.await()
    }

    suspend fun saveAnonToEmail(email: String, password: String){
        val credential = EmailAuthProvider.getCredential(email, password)

        user!!.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveLastLogin()
                    getUser()
                }
            }.addOnFailureListener {

            }.await()
    }

    suspend fun updateEmail(email: String){
        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveLastLogin()
                }
            }.addOnFailureListener {

            }.await()
    }

    suspend fun updatePassword(password: String){
        user!!.updatePassword(password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }.addOnFailureListener {

            }.await()

    }

    private fun saveLastLogin(){
        getUser()
        sp.edit().putString("email", user?.email).apply()
    }

    fun getLastLogin() : String?{
        return sp.getString("email", "")
    }

    fun checkNewUser() : Boolean{
        return isNewUser
    }

    suspend fun deleteUser(){
        user!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signOut()
                }
            }.addOnFailureListener {

            }.await()
    }

    fun signOut(){
        auth.signOut()
    }
}