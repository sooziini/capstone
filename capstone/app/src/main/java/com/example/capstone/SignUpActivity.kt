package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class   SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // 뒤로가기 버튼 -> 로그인 화면으로 돌아감
        SignUpGoBackButton.setOnClickListener {
            startActivity<LoginActivity>()
        }

        // 학년 드롭다운 스피너
//        val gradeList = Array(3, {i -> i + 1})     // 학년 드롭다운 배열
        val gradeList = arrayOf("학년", "1", "2", "3")
        SignUpGradeDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gradeList)
        SignUpGradeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
//                println("학년")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                println("$p2")
            }
        }

        // 반 드롭다운 스피너
//        val classList = Array(8, {i -> i + 1})     // 반 드롭다운 배열
        val classList = arrayOf("반", "1", "2", "3", "4", "5", "6", "7", "8")
        SignUpClassDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classList)
        SignUpClassDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
//                println("반")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                println("$p2")
            }
        }


        // 통신사 드롭다운 스피너
        val phoneList = arrayOf("통신사", "SKT", "KT", "LG")      // 통신사 드롭다운 배열
        SignUpPhoneDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, phoneList)
        SignUpPhoneDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
//                println("통신사")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                println("$p2")
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

//        val regData = RegData(""
        val nickname = SignUpNicknameEditTextView.text.toString()
        val id = SignUpIdEditTextView.text.toString()
        val password = SignUpPasswordEditTextView.text.toString()
        val name = SignUpNameEditTextView.text.toString()
        val birth = SignUpBirthEditText.text.toString()
        val stuGrade = SignUpGradeDropdown.getSelectedItem().toString().toInt()
        val stuClass = SignUpClassDropdown.getSelectedItem().toString().toInt()
        val phoneNum = SignUpPhoneEditText.text.toString()
        val agency = SignUpPhoneDropdown

        val regData = HashMap<String, String>()

        // 가입 구현
        // 닉네임, ID, (학년,반,번호), (통신사, 휴대폰 번호) 중복되지 않을 시
        // & 모든 칸에 빈칸이 없다면

        regData.put("id", id)
        regData.put("password", password)
        regData.put("name", name)
        regData.put("phone", phoneNum)
        regData.put("nickname", nickname)
        regData.put("birth", birth)

//        if (1 <= stuGrade && stuGrade <= 3) {
//            alert("학년을 선택해 주세요") {
//                yesButton {
//
//                }
//            }
//        }
//        else {
//            regData.put("schoolgrade", stuGrade)
//        }
//
//        if (1 <= stuClass && stuClass <= 8 ) {
//            alert ("반을 선택해 주세요"){
//                yesButton {
//
//                }
//            }
//        }
//        else {
//            regData.put("schoolclass", stuClass)
//        }

        // 입력받은 회원정보 POST
        (application as MasterApplication).service.signUp(regData)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result!!.get("success") == "true") {
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        } else {
                            toast("회원가입 실패")
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

    private fun checkNicknameDup() {
        // 닉네임 중복확인 버튼 기능구현
        (application as MasterApplication).service.confirmNickname()
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result!!.get("success") == "true") {
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        } else {
                            toast("회원가입 실패")
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

    private fun checkIdDup() {
        // ID 중복확인 버튼 기능구현
        (application as MasterApplication).service.confirmId()
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result!!.get("success") == "true") {
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        } else {
                            toast("회원가입 실패")
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

    private fun sendNum() {
        // 인증번호 전송 기능구현
    }
}