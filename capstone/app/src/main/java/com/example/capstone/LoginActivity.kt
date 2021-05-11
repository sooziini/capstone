package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 클릭
        LoginRegisterButton.setOnClickListener {
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
                    .enqueue(object : Callback<HashMap<String, String>> {
                        override fun onResponse(
                            call: Call<HashMap<String, String>>,
                            response: Response<HashMap<String, String>>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result!!.get("success") == "true") {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                } else {
                                    toast("로그인 실패")
                                }
                            } else {
                                toast("error")
                            }
                        }

                        // 응답 실패 시
                        override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                            toast("network error")
                        }
                    })
            }

        }

    }
}