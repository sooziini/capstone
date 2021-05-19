package com.example.capstone

import com.example.capstone.dataclass.PostList
import com.example.capstone.dataclass.RegData
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

    @POST("user/login")
    fun login(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, Any>>

    @POST ("user/register")
    fun signUp(
        @Body params: HashMap<String, Any>
    ): Call<HashMap<String, String>>

    @GET("user/confirm/name")
    fun confirmId(): Call<HashMap<String, String>>

    @GET("user/confirm/nickname")
    fun confirmNickname(): Call<HashMap<String, String>>
}