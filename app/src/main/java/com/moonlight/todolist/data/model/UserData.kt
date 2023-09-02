package com.moonlight.todolist.data.model

import java.io.Serializable

data class UserData(
    var uid: String = "",
    var userName: String = "",
    var categoryList: List<String> = emptyList()
) : Serializable