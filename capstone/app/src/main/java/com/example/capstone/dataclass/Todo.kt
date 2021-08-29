package com.example.capstone.dataclass

import java.io.Serializable

data class Todo(
    val list_id: Int,
    val body: String,
    val checked: String
):Serializable