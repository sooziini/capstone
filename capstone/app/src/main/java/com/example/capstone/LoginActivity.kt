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

        LoginRegisterButton.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        LoginFindIdPassword.setOnClickListener {
            // 추후 구현
        }

        LoginButton.setOnClickListener {
            if (LoginIdEditText.text != null && LoginPasswordEditText.text != null) {
                    startActivity<MainActivity>()
            } else if (LoginIdEditText.text == null || LoginPasswordEditText.text == null){
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