package com.moonlight.todolist.ui.main.archivedItems

import androidx.lifecycle.ViewModel
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.repo.ToDoRepository
import com.moonlight.todolist.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArchivedItemsViewModel @Inject constructor(private var trepo: ToDoRepository, private var urepo: UserRepository) : ViewModel() {

}