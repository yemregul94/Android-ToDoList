package com.moonlight.todolist.data.datasource

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.moonlight.todolist.data.model.UserData
import kotlinx.coroutines.tasks.await

class UserDataSource (var refDB: DatabaseReference, var sp: SharedPreferences) {

    fun updateUserData(user: UserData, uid: String?){
        if(!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).child("UserData").setValue(user)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                    }
                }.addOnFailureListener {
            }
        }
    }

    fun getUserData(uid: String?) : MutableLiveData<UserData?> {

        val userLiveData = MutableLiveData<UserData?>()
        if(!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).child("UserData").addValueEventListener(object :
                ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserData::class.java)
                    userLiveData.value = user
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }

            })
        }
        return userLiveData
    }

    suspend fun deleteUserData(uid: String?){
        if(!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).removeValue().await()
        }
    }

    fun getTheme(): String? {
        return sp.getString("theme", "System")
    }

    fun saveTheme(mode: String){
        sp.edit().putString("theme", mode).apply()
    }

    fun getSortIndex() : String? {
        return sp.getString("sort", "last updated")
    }

    fun saveSortIndex(sort: String){
        sp.edit().putString("sort", sort).apply()
    }



}