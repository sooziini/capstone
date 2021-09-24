package com.example.capstone.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.capstone.R
import com.example.capstone.main.MainActivity
import com.example.capstone.master.MainActivity2
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var mBackWait:Long = 0

    // 키보드 InputMethodManager 변수 선언
    private var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val app = application as MasterApplication

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 회원가입 클릭
        LoginRegisterBtn.setOnClickListener {
            startActivity<SignUpActivity>()
            finish()
        }

        // 아이디, 비밀번호 찾기 클릭
        LoginFindIdPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindPasswordActivity::class.java))
            finish()
        }

        // 로그인 버튼   
        LoginButton.setOnClickListener {
            val id = LoginIdEditText.text.toString()
            val password = LoginPasswordEditText.text.toString()
            val post = HashMap<String, String>()

            when {
                id == "" -> {
                    toast("아이디를 입력해 주세요")
                }
                password == "" -> {
                    toast("비밀번호를 입력해 주세요")
                }
                else -> {
                    post["id"] = id
                    post["password"] = password

                    // 입력받은 id와 password POST
                    app.service.login(post)
                        .enqueue(object : Callback<HashMap<String, Any>> {
                            override fun onResponse(
                                call: Call<HashMap<String, Any>>,
                                response: Response<HashMap<String, Any>>
                            ) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    val tokenMap: LinkedTreeMap<String, String>
                                    var accessToken: String? = null
                                    var refreshToken: String? = null

                                    if (result!!["token"] != null) {
                                        tokenMap = result["token"] as LinkedTreeMap<String, String>
                                        accessToken = tokenMap["access_token"].toString()
                                        refreshToken = tokenMap["refresh_token"].toString()
                                    }

                                    if (accessToken == null || refreshToken == null) {
                                        Toast.makeText(this@LoginActivity, "아이디와 비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show()
                                    } else {
                                        app.saveUserToken("access_token", accessToken)
                                        app.saveUserToken("refresh_token", refreshToken)

                                        sendRegistrationToServer()

                                        if (result["role"] == "master") {   // master 로그인
                                            startActivity(Intent(this@LoginActivity, MainActivity2::class.java))
                                            finish()
                                        } else {      // 일반계정 로그인 (학생, 학생회)
                                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                            finish()
                                        }
                                    }
                                } else {        // 3xx, 4xx 를 받은 경우
                                    toast("아이디와 비밀번호가 일치하지 않습니다")
                                }
                            }
                            // 응답 실패 시
                            override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                                toast("network error")
                                finish()
                            }
                        })
                }
            }

        }
    }

    // firebase 토큰을 서버로 전송
    fun sendRegistrationToServer() {
        val sp = getSharedPreferences("firebase", Context.MODE_PRIVATE)
        val token = sp.getString("token", null)

        (application as MasterApplication).service.setDeviceToken(token!!)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        //
                    } else {
                        toast("알림 설정 오류")
                        finish()
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - mBackWait >= 2000 ) {
            mBackWait = System.currentTimeMillis()
            toast("뒤로가기 버튼을 한번 더 누르면 종료됩니다")
        } else {
            finish()
        }
    }
}