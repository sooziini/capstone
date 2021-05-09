package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton

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
            if (LoginIdEditText.text.isNotEmpty() && LoginPasswordEditText.text.isNotEmpty()) {
                // ID 비밀번호 확인 조건 필요
                startActivity<MainActivity>()
            } else if (LoginIdEditText.text.isEmpty() || LoginPasswordEditText.text.isEmpty()){
                alert("ID와 비밀번호를 모두 입력하세요") {
                    yesButton {  }
                }.show()
//        } else if (IdEditText.text != null && PasswordEditText.text != null) {
//
            } else {

            }
        }

    }
}