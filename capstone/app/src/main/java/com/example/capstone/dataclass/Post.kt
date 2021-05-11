package com.example.capstone.dataclass

import java.io.Serializable
import java.sql.Date

data class PostList (
    val success: String,
    val data: ArrayList<Post>
): Serializable

data class Post (
    val board_id: Int,
    val title: String,
    val body: String
    // val user_id: String
    // val regdate: Date
): Serializable