package com.moonlight.todolist.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.repo.ToDoRepository
import com.moonlight.todolist.data.repo.UserRepository
import com.moonlight.todolist.util.DATE_FORMAT
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private var trepo: ToDoRepository, private var urepo: UserRepository) : ViewModel() {
    private var uid = ""

    val toDoListsItem: LiveData<List<ToDoListItem>> get() = _toDoListsItem
    private var _toDoListsItem: MutableLiveData<List<ToDoListItem>> = MutableLiveData()

    val filteredList: LiveData<List<ToDoListItem>> get() = _filteredList
    private val _filteredList: MutableLiveData<List<ToDoListItem>> = MutableLiveData()

    var selectedCategory: String = ""
    var selectedCategoryIndex: Int = 0
    var hideCompleted: Boolean = false
    var filterFavorites: Boolean = false
    var searchQuery: String = ""
    var sortCriteria: String = "last updated"
    var priority: Int = -1


    fun setUID(uid: String){
        this.uid = uid
        loadListItems()
    }

    private fun loadListItems(){
        _toDoListsItem = trepo.getListItems(uid)
    }

    fun deleteListItems(listID: String){
        trepo.deleteListItem(listID, uid)
    }

    fun toggleComplete(list: ToDoListItem){
        val newList = list.copy(completed = !list.completed)
        trepo.updateListItem(newList, uid)
    }

    fun toggleSubTaskComplete(list: ToDoListItem, taskPosition: Int){
        list.subTaskList!![taskPosition].completed = !list.subTaskList!![taskPosition].completed
        trepo.updateListItem(list, uid)
    }

    fun toggleFavorite(list: ToDoListItem){
        val newList = list.copy(favorite = !list.favorite)
        trepo.updateListItem(newList, uid)
    }

    fun undoDelete(list: ToDoListItem){
        trepo.newListItem(list, uid)
    }

    fun applyFilters() {

        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

        if(toDoListsItem.value != null){

            var filterList = toDoListsItem.value

            if(hideCompleted){
                filterList = filterList?.filterNot { it.completed }
            }
            if(filterFavorites){
                filterList = filterList?.filter { it.favorite }
            }
            if (selectedCategoryIndex != 0){
                filterList = filterList?.filter {
                    it.category == selectedCategory
                }
            }
            filterList = filterList?.filter { it.title.contains(searchQuery, true) }

            filterList = when(sortCriteria){
                "title" -> { filterList?.sortedBy { it.title.lowercase() } }
                "title reverse" -> { filterList?.sortedByDescending { it.title.lowercase() } }
                "first created" -> { filterList?.sortedBy { dateFormat.parse(it.createTime) } }
                "last created" -> { filterList?.sortedByDescending { dateFormat.parse(it.createTime) } }
                "first updated" -> { filterList?.sortedBy { dateFormat.parse(it.updateTime) } }
                else -> { filterList?.sortedByDescending { dateFormat.parse(it.updateTime) } }
            }

            if(priority != -1){
                filterList = filterList?.filter { it.priority == priority }
            }

            _filteredList.value = filterList!!
        }
    }

    fun getSortIndex(): String?{
        return urepo.getSortIndex()
    }

    fun saveSortIndex(sortIndex: String){
        urepo.saveSortIndex(sortIndex)
    }
}