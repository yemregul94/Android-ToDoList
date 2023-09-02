package com.moonlight.todolist.data.model

import java.io.Serializable

data class ToDoListExpand(
    var id: String = "",
    var expandStatus: Boolean = false
) : Serializable