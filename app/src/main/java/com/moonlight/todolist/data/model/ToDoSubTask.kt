package com.moonlight.todolist.data.model

import java.io.Serializable

data class ToDoSubTask(
    var id: String = "",
    var title: String = "",
    var completed: Boolean = false
) : Serializable