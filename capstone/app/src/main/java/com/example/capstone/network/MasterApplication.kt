package com.example.capstone.network

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.capstone.dataclass.NotiPost
import okhttp3.*
import org.jetbrains.anko.toast
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication: Application(), LifecycleObserver {
    lateinit var service: RetrofitService

    val BASE_URL = "http://220.149.31.104:3000"     //220.149.31.104

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        createRetrofit(null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("abc", "back"+isInForeground().toString())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("abc", "fore"+isInForeground().toString())
    }

    fun isInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    // retrofit 생성하는 함수
    fun createRetrofit(refreshToken: String?) {
        // header 설정 (header에 token이 있는 retrofit)
        // 원래 나가려던 통신을 original에 잡아둠
        // original에 header 추가 -> proceed
        val header = Interceptor {
            val original = it.request()

            if (checkIsLogin()) {
                getUserToken(0).let { token ->
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
            .addInterceptor(header)
            .apply {
                if (refreshToken != null)   // refresh token으로 retrofit 재설정
                    authenticator(TokenAuthenticator(refreshToken, this@MasterApplication))
                else {
                    if(checkIsLogin())
                        authenticator(TokenAuthenticator(getUserToken(1)!!, this@MasterApplication))
                }
            }.build()

        // retrofit 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("$BASE_URL/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    // accessToken으로 현재 로그인 상태인지 확인하는 함수
    fun checkIsLogin(): Boolean {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("access_token", null)

        return token != null
    }

    // ver == 0 accessToken return
    // ver == 1 refreshToken return
    // ver == 2 (else) role return
    fun getUserToken(ver: Int): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = when (ver) {
            0 -> sp.getString("access_token", "null")
            1 -> sp.getString("refresh_token", "null")
            else -> sp.getString("role", "null")
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
        editor.remove("role")
        editor.apply()
    }

    // 토큰 재발급 함수
    fun retrofitSetRefreshToken(token: String, mContext: Context) {
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
                    } else if (response.code() == 401) {
                        deleteUserToken()
                        (mContext as Activity).finish()
                    } else {
                        (mContext as Activity).finish()
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    (mContext as Activity).finish()
                }
            })
    }

    // 알림 추가 함수
    fun retrofitCreateNotification(notiList: HashMap<String, ArrayList<NotiPost>>) {
        service.createNotification(notiList)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        // 알림 추가 성공
                    } else {
                        // 알림 추가 실패
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    // 알림 추가 실패
                }
            })
    }
}