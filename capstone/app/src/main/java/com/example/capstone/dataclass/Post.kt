package com.example.capstone.dataclass

import java.io.Serializable

data class PostList (
    val success: String,
    val data: ArrayList<Post>
): Serializable

data class Post (
//    val board_img: Int, //게시판 별 아이콘 이미지
    //val board_id: Int,
    val title: String,
    val body: String,
    val user_id: String,
    val regdate: String,
    val goodCount: Int
): Serializable