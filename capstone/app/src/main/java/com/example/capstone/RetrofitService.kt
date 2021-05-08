package com.example.capstone

import com.example.capstone.dataclass.Post
import com.example.capstone.dataclass.PostList
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {
    // @Headers("content-type: application/json")
    @GET("board/")
    fun getPostList(): Call<PostList>
}