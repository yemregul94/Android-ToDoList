package com.moonlight.todolist.data.model

import java.io.Serializable

data class ToDoListItem(
    var id: String? = "",
    var title: String = "",
    var desc: String? = "",
    var priority: Int = 1,
    var completed: Boolean = false,
    var category: String = "",
    var subTaskList: List<ToDoSubTask>? = null,
    var color: String = "",
    var favorite: Boolean = false,

    var createTime: String = "",
    var updateTime: String = ""
) : Serializable