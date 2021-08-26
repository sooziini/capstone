package com.example.capstone.network

import android.app.Application
import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication: Application() {
    lateinit var service: RetrofitService
    val BASE_URL = "http://192.168.0.2:3000"

    override fun onCreate() {
        super.onCreate()

        createRetrofit()
    }

    // retrofit 생성하는 함수
    fun createRetrofit() {
        // header 설정 (header에 token이 있는 retrofit)
        // 원래 나가려던 통신을 original에 잡아둠
        // original에 header 추가 -> proceed
        val header = Interceptor {
            val original = it.request()

            if (checkIsLogin()) {
                getUserToken().let { token ->
                    val request = original.newBuilder()
                        .header("Authorization", token)
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
    // 해당 key의 값이 없으면 login X
    // -> token 값이 없으면 login X
    private fun checkIsLogin(): Boolean {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var token = sp.getString("access_token", null)

        return token != null
    }

    private fun getUserToken(): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("access_token", "null")

        return if (token == "null") null
        else token
    }
}