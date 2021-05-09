package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import org.jetbrains.anko.startActivity

class   SignUpActivity : AppCompatActivity() {
    val classList = Array(8, {i -> i + 1})     // 반 드롭다운 배열
    val gradeList = Array(3, {i -> i + 1})     // 학년 드롭다운 배열
    val phoneList = arrayOf("SKT", "KT", "LG")      // 통신사 드롭다운 배열

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // 뒤로가기 버튼 -> 로그인 화면으로 돌아감
        SignUpGoBackButton.setOnClickListener {
            startActivity<LoginActivity>()
        }

        // 학년 드롭다운 스피너
        SignUpGradeDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gradeList)
        SignUpGradeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("학년")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("$p2")
            }
        }

        // 반 드롭다운 스피너
        SignUpClassDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classList)
        SignUpClassDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("반")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("$p2")
            }
        }


        // 통신사 드롭다운 스피너
        SignUpPhoneDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, phoneList)
        SignUpPhoneDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("통신사")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("$p2")
            }
        }

        // 닉네임 중복확인 버튼
        SignUpNicknameButton.setOnClickListener {
            checkNicknameDup()
        }

        // ID 중복확인 버튼
        SignUpIdButton.setOnClickListener {
            checkIdDup()
        }

        // 인증번호 받기 버튼
        SignUpSendNumButton.setOnClickListener {
            sendNum()
        }

        SignUpButton.setOnClickListener {       // 회원가입 버튼
            signUp()        // 가입 메소드
        }
    }

    private fun signUp() {
        val nickname = SignUpNicknameEditTextView.text
        val id = SignUpIdEditTextView.text
        val password = SignUpPasswordEditTextView.text
        val name = SignUpNameEditTextView.text
        val birth = SignUpBirthEditText.text
        val stuGrade = SignUpGradeDropdown
        val stuClass = SignUpClassDropdown
        val stuNum = SignUpNumberEditText.text
        val agency = SignUpPhoneDropdown
        val phoneNum = SignUpPhoneEditText.text

        val result = arrayOf(nickname, id, password, name, birth, stuGrade, stuClass, stuNum, agency, phoneNum)

        // 가입 구현
        // 닉네임, ID, (학년,반,번호), (통신사, 휴대폰 번호) 중복되지 않을 시 가입
    }

    private fun checkNicknameDup() {
        // 닉네임 중복확인 버튼 기능구현
    }

    private fun checkIdDup() {
        // ID 중복확인 버튼 기능구현
    }

    private fun sendNum() {
        // 인증번호 전송 기능구현
    }
}