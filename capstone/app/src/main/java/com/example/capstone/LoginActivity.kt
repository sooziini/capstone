package com.example.capstone

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    // 키보드 InputMethodManager 변수 선언
    var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 회원가입 클릭
        LoginRegisterBtn.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        // 아이디, 비밀번호 찾기 클릭
        LoginFindIdPassword.setOnClickListener {
            // 구현
        }

        // 로그인 버튼   
        LoginButton.setOnClickListener {
            val id = LoginIdEditText.text.toString()
            val password = LoginPasswordEditText.text.toString()
            val post = HashMap<String, String>()

            if (id == "") {
                toast("ID를 입력해주세요")
            } else if (password == "") {
                toast("비밀번호를 입력해주세요")
            } else {
                post.put("id", id)
                post.put("password", password)

                // 입력받은 id와 password POST
                (application as MasterApplication).service.login(post)
                    .enqueue(object : Callback<HashMap<String, Any>> {
                        override fun onResponse(
                            call: Call<HashMap<String, Any>>,
                            response: Response<HashMap<String, Any>>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                //val token = response.headers().get("X-AUTH-TOKEN").toString()
                                var tokenMap: LinkedTreeMap<String, String>
                                var token: String = "null"

                                if (result!!.get("token") != null) {
                                    tokenMap = result!!.get("token") as LinkedTreeMap<String, String>
                                    token = tokenMap?.get("access_token").toString()
                                }

                                if (token == "null") {
                                    Toast.makeText(this@LoginActivity, "아이디, 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                                } else {
                                    saveUserToken(token, this@LoginActivity)

//                                (application as MasterApplication).createRetrofit()

//                                Toast.makeText(this@LoginActivity, "${result!!.get("id")}" + "님 환영합니다 !", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                                    .putExtra("userId", result.get("id").toString()))

                                }

//                            if (result!!.get("success") == "true") {
//                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                            } else {
//                                toast("로그인 실패")
//                            }
                            } else {        // 3xx, 4xx 를 받은 경우
                                toast("로그인 실패")
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

    fun saveUserToken(token: String, activity: Activity) {
        val sp = activity.getSharedPreferences("login_token", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_token", token)
        editor.apply()
    }

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}