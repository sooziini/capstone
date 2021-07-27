package com.example.capstone.network

import com.example.capstone.dataclass.PostDetail
import com.example.capstone.dataclass.PostList
import com.example.capstone.dataclass.ReplyListList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    // @Headers("content-type: application/json")
    // 게시글 목록 조회
    @GET("board/")
    fun getPostList(
        @Query("type") type: String
    ): Call<PostList>

    // 게시글 생성
    @Multipart
    @POST("board/")
    fun createPost(
        @Part("title") title: String,
        @Part("body") body: String,
        @Part images: ArrayList<MultipartBody.Part>
    ): Call<HashMap<String, String>>

    // 게시글 자세히보기
    @GET("board/{boardid}/")
    fun getPostDetail(
        @Path("boardid") board_id: String
    ): Call<PostDetail>

    // 게시글 검색
    @GET("board/search/")
    fun searchPostList(
        @Query("title") title: String
    ): Call<PostList>

    // 게시글 수정
    @Multipart
    @PUT("board/{boardid}")
    fun putPostDetail(
        @Path("boardid") board_id: String,
        @Part("title") title: String,
        @Part("body") body: String,
        @Part images: ArrayList<MultipartBody.Part>
    ): Call<HashMap<String, String>>

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

    // 댓글 등록
    @POST("reply/{boardid}")
    fun createReply(
        @Path("boardid") board_id: String,
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>

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