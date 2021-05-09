package com.example.capstone

import com.example.capstone.dataclass.PostList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {
    // @Headers("content-type: application/json")
    @GET("board/")
    fun getPostList(): Call<PostList>

    @POST("board/")
    fun createPost(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>
}