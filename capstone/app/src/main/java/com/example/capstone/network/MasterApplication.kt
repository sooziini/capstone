package com.example.capstone.network

import android.app.Application
import android.content.Context
import com.example.capstone.SplashActivity
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.toast
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MasterApplication: Application() {
    lateinit var service: RetrofitService

    val BASE_URL = "http://220.149.31.104:3000"
    //220.149.31.104

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

        val clientBuilder = OkHttpClient.Builder()
            .apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            .addInterceptor(header)
            if(checkIsLogin())
                clientBuilder.authenticator(TokenAuthenticator(getUserToken(1)!!))
        val client = clientBuilder.build()

        // retrofit 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("$BASE_URL/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    // authenticator 포함 후 레트로핏 재설정
    fun updateRetrofit(refreshToken: String) {
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
            .apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            .addInterceptor(header)
            .authenticator(TokenAuthenticator(refreshToken))
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
                    } else {
                        (mContext as SplashActivity).finish()
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    (mContext as SplashActivity).finish()
                }
            })
    }
}
/////////////////////////////////////////// 토큰 만료시 재발급

class TokenAuthenticator(
    private val refreshToken: String,
): Authenticator {
    companion object {
        private val TAG = TokenAuthenticator::class.java.simpleName
    }

    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
        if (response.code() == 401) {
//            val refreshToken = CommonHelper.getRefreshToken(sharedPref)
            val getNewDeviceToken = GlobalScope.async(Dispatchers.Default) {
                getNewDeviceToken(refreshToken)
            }

            val token = runBlocking {
                getNewDeviceToken.await()
            }
            if (token != null) {
                return getRequest(response, token)
            }
        }
        return null
    }

    private suspend inline fun getNewDeviceToken(token: String): String? {
        return GlobalScope.async(Dispatchers.Default) {
            callApiNewDeviceToken(token)
        }.await()
    }

    private suspend inline fun callApiNewDeviceToken(token: String): String? = suspendCoroutine { continuation ->
        createWebService<RetrofitService>()
            .setRefreshToken(token)
//            .with(rx)
            .enqueue(object: Callback<HashMap<String, String>> {
                override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["access_token"]!!
                        MasterApplication().saveUserToken("access_token", data)
                        continuation.resume(data)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    continuation.resume(null)
                }
            })
        return@suspendCoroutine
    }

    private val okHttp = OkHttpClient.Builder()
//        .connectTimeout(TIMEOUT_LIMIT, TimeUnit.SECONDS)
//        .readTimeout(TIMEOUT_LIMIT, TimeUnit.SECONDS)
//        .writeTimeout(TIMEOUT_LIMIT, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private inline fun <reified T> createWebService(): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(MasterApplication().BASE_URL + "/api/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            ))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
            .build()
        return retrofit.create(T::class.java)
    }

    private fun getRequest(response: okhttp3.Response, token: String): Request {
        return response.request()
            .newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", token)
            .build()
    }
}