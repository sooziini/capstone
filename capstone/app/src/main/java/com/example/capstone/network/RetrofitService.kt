package com.example.capstone.network

import com.example.capstone.dataclass.PostDetail
import com.example.capstone.dataclass.PostList
import com.example.capstone.dataclass.ReplyListList
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    // @Headers("content-type: application/json")
    // 게시글 목록 조회
    @GET("board/")
    fun getPostList(): Call<PostList>

    // 게시글 생성
    @POST("board/")
    fun createPost(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>

    // 게시글 자세히보기
    @GET("board/{boardid}/")
    fun getPostDetail(
        @Path("boardid") board_id: String
    ): Call<PostDetail>

    // 게시글 검색
    @GET("board/search")
    fun searchPostList(
        @Query("title") title: String
    ): Call<PostList>

    // 게시글 삭제
    @DELETE("board/{boardid}/")
    fun deletePostDetail(
        @Path("boardid") board_id: String
    ): Call<HashMap<String, String>>

    // 댓글 조회
    @GET("reply/{boardid}")
    fun getReplyList(
        @Path("boardid") board_id: String
    ): Call<ReplyListList>

    // 로그인
    @POST("user/login")
    fun login(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, Any>>

    // 회원가입
    @POST ("user/register")
    fun signUp(
        @Body params: HashMap<String, Any>
    ): Call<HashMap<String, String>>

    // 아이디 중복 확인
    @POST("user/confirm/name")
    fun confirmId(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>

    // 닉네임 중복 확인
    @POST("user/confirm/nickname")
    fun confirmNickname(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>
}