package com.example.capstone.network

import com.example.capstone.dataclass.PostDetail
import com.example.capstone.dataclass.PostList
import com.example.capstone.dataclass.ReplyListList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
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
        @Query("type") type: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part images: ArrayList<MultipartBody.Part>
    ): Call<HashMap<String, Any>>

    // 게시글 자세히보기
    @GET("board/{boardid}/")
    fun getPostDetail(
        @Path("boardid") board_id: String
    ): Call<PostDetail>

    // 게시글 검색
    @GET("board/search/")
    fun searchPostList(
        @Query("type") type: String,
        @Query("title") title: String
    ): Call<PostList>

    // 게시글 수정
    @Multipart
    @PUT("board/{boardid}")
    fun putPostDetail(
        @Path("boardid") board_id: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part images: ArrayList<MultipartBody.Part>
    ): Call<HashMap<String, String>>

    // 게시글 삭제
    @DELETE("board/{boardid}/")
    fun deletePostDetail(
        @Path("boardid") board_id: String
    ): Call<HashMap<String, String>>

    // 게시글 스크랩
    @GET("board/scrap/{boardid}")
    fun scrapPost(
        @Path("boardid") board_id: String
    ): Call<HashMap<String, String>>

    // 게시글 좋아요
    @GET("board/good/{boardid}")
    fun goodPost(
        @Path("boardid") board_id: String
    ): Call<HashMap<String, String>>

    // 게시글 신고
    @POST("board/report")
    fun reportPost(
        @Body params: HashMap<String, Any>
    ): Call<HashMap<String, String>>

    // 댓글 조회
    @GET("reply/{boardid}")
    fun getReplyList(
        @Path("boardid") board_id: String
    ): Call<ReplyListList>

    // 댓글 등록
    @POST("reply/{boardid}")
    @FormUrlEncoded
    fun createReply(
        @Path("boardid") board_id: String,
        @Field("body") body: String
    ): Call<HashMap<String, Any>>

    // 대댓글 등록
    @POST("reply/{boardid}/{replyid}")
    @FormUrlEncoded
    fun createReplyReply(
        @Path("boardid") board_id: String,
        @Path("replyid") reply_id: String,
        @Field("body") body: String
    ): Call<HashMap<String, Any>>

    // 댓글 삭제
    @DELETE("reply/{boardid}/{replyid}")
    fun deleteReply(
        @Path("boardid") board_id: String,
        @Path("replyid") reply_id: String
    ): Call<HashMap<String, String>>

    // 댓글 좋아요
    @GET("reply/good/{replyid}")
    fun goodReply(
        @Path("replyid") reply_id: String
    ): Call<HashMap<String, String>>

    // 댓글 신고
    @POST("reply/report")
    fun reportReply(
        @Body params: HashMap<String, Any>
    ): Call<HashMap<String, String>>

    // 스크랩한 게시글 목록 조회
    @GET("board/scrap")
    fun getScrapPostList(): Call<PostList>

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

    // 학번 인증
    @POST("user/auth/student/check")
    fun authStudent(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>

    // 로그아웃
    @POST("user/logout")
    fun logout(): Call<HashMap<String, String>>

    // 토큰 검증 (회원 데이터 조회)
    @GET("auth/valid")
    fun authorization():Call<HashMap<String, Any>>

    // 비밀번호 찾기
    @POST("user/password/find")
    fun findPassword(
        @Body params: HashMap<String, String>
    ):Call<HashMap<String, String>>

    // 식단표 받아오기
    @GET("school/cafeteria?")
    fun loadMeal(
        @Query ("MLSV_FROM_YMD") start: String,
        @Query ("MLSV_TO_YMD") end: String
    ): Call<HashMap<String, Any>>

    // 비밀번호 확인
    @POST("user/password/check")
    fun checkPassword(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>

    // 비밀번호 변경
    @POST("user/password/change")
    fun changePassword(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, String>>

    // 회원탈퇴
    @DELETE("user/quit")
    fun deleteUser():Call<HashMap<String, String>>

    // 본인 정보 조회
    @GET("user/info")
    fun readInfo():Call<HashMap<String, Any>>

    // 본인 정보 수정
    @PUT("user/info")
    fun updateInfo(
        @Body params: HashMap<String, String>
    ): Call<HashMap<String, Any>>
}