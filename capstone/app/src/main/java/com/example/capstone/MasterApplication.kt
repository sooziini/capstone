package com.example.capstone

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication: Application() {
    lateinit var service: RetrofitService

    override fun onCreate() {
        super.onCreate()

        createRetrofit()
    }

    // retrofit 생성하는 함수
    fun createRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("/")
            .addConverterFactory(GsonConverterFactory.create())
            // .client(client)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }
}