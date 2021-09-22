package com.example.capstone.network

import android.app.Application
import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication: Application() {
    lateinit var service: RetrofitService
    val BASE_URL = "http://192.168.0.2:3000"
    //220.149.31.104

    override fun onCreate() {
        super.onCreate()

        createRetrofit()
    }

    // retrofit 생성하는 함수
    private fun createRetrofit() {
        // header 설정 (header에 token이 있는 retrofit)
        // 원래 나가려던 통신을 original에 잡아둠
        // original에 header 추가 -> proceed
        val header = Interceptor {
            val original = it.request()

            if (checkIsLogin()) {
                getUserToken(true).let { token ->
                    val request = original.newBuilder()
                        .header("Authorization", token!!)
                        .build()
                    it.proceed(request)
                }
            } else {
                it.proceed(original)
            }
        }

        val client = OkHttpClient.Builder()
            .apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            .addInterceptor(header)
            .build()

        // retrofit 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("$BASE_URL/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    // SharedPreferences에 token 값 저장되어 있음
    // accessToken으로 현재 로그인 상태인지 확인하는 함수
    fun checkIsLogin(): Boolean {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("access_token", null)

        return token != null
    }

    // ver == true accessToken return
    // ver == false refreshToken return
    fun getUserToken(ver: Boolean): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = if (ver) {
            sp.getString("access_token", "null")
        } else {
            sp.getString("refresh_token", "null")
        }

        return if (token == "null") null
        else token
    }

    // 토큰 저장
    fun saveUserToken(name: String, token: String) {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(name, token)
        editor.apply()
    }

    // 토큰 삭제
    fun deleteUserToken() {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove("access_token")
        editor.remove("refresh_token")
        editor.apply()
    }

    // 토큰 재발급 함수
    fun retrofitSetRefreshToken(token: String) {
        service.setRefreshToken(token)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        val accessToken = response.body()!!["access_token"]
                        val refreshToken = response.body()!!["refresh_token"]

                        saveUserToken("access_token", accessToken!!)
                        if (refreshToken != null && refreshToken != "")
                            saveUserToken("refresh_token", refreshToken)
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                }
            })
    }
}