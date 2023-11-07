package com.moonlight.todolist.data.datasource

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.util.setAlarm

class ToDoDataSource(var refDB: DatabaseReference) {

    fun newListItem(listItem: ToDoListItem, uid: String?){
        if(!uid.isNullOrEmpty()){
            refDB.child("ToDoApp").child(uid).child("ToDoListItems").push().setValue(listItem).addOnCompleteListener {
                if(it.isSuccessful){

                }
            }.addOnFailureListener {
            }
        }
    }

    fun getListItems(uid: String?) : MutableLiveData<List<ToDoListItem>> {

        val lists = MutableLiveData<List<ToDoListItem>>()

        if(!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).child("ToDoListItems")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = ArrayList<ToDoListItem>()

                        for (d in snapshot.children) {
                            val item = d.getValue(ToDoListItem::class.java)

                            if (item != null) {
                                item.id = d.key!!
                                list.add(item)
                            }
                        }
                        lists.value = list
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        return lists
    }

    fun updateListItem(listItem: ToDoListItem, uid: String?){
        if(!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).child("ToDoListItems").child(listItem.id!!)
                .setValue(listItem).addOnCompleteListener {
                if (it.isSuccessful) {

                }
            }.addOnFailureListener {
            }
        }
    }

    fun deleteListItem(listID: String, uid: String?) {
        if (!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).child("ToDoListItems").child(listID).removeValue()
        }
    }

    fun getAlarms(context: Context?, uid: String?){
        if(!uid.isNullOrEmpty()) {
            refDB.child("ToDoApp").child(uid).child("ToDoListItems")
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (d in snapshot.children) {
                            val item = d.getValue(ToDoListItem::class.java)

                            if (item != null && item.alarmTime.isNotEmpty()) {
                                setAlarm(context!!, item)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }
    }
}