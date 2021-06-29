package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class   SignUpActivity : AppCompatActivity() {

    // 키보드 InputMethodManager 변수 선언
    var imm: InputMethodManager? = null

    var idConfirm: Boolean = false
    var nicknameConfirm: Boolean = false


    class MyEditWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            Log.d("", "beforeTextChanged: $s")
        }
        override fun afterTextChanged(s: Editable?) {
            Log.d("", "afterTextChanged: $s")
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.d("", "onTextChanged: $s")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // toolbar 설정
        setSupportActionBar(sign_up_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

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

        var watcher = MyEditWatcher()

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

        val id = SignUpIdEditTextView.text.toString()
        val password = SignUpPasswordEditTextView.text.toString()
        val passwordconfirm = SignUpPassWordCheckEditTextView.text.toString()
        val name = SignUpNameEditTextView.text.toString()
        val phoneNum = SignUpPhoneEditText.text.toString()
        val birth = SignUpBirthEditText.text.toString()
        val stuGrade = SignUpGradeDropdown.getSelectedItem().toString().toInt()
        val stuClass = SignUpClassDropdown.getSelectedItem().toString().toInt()

        if(password != passwordconfirm) {
            alert("비밀번호를 확인해 주세요") {
                yesButton {  }
            }
            return
        }

        if (!(1 <= stuGrade && stuGrade <= 3)) {
            // 맞는 학년 데이터가 아닌 경우
            alert("학년을 선택해 주세요") {
                yesButton { }
            }
            return
        }

        if (!(1 <= stuClass && stuClass <= 8)) {
            // 맞는 반 데이터가 아닌 경우
            alert ("반을 선택해 주세요"){
                yesButton { }
            }
            return
        }

        if (id == "" || password == "" || passwordconfirm == "" || name == "" || phoneNum == "" || birth == "") {
            alert("빈칸 없이 입력해주세요"){
                yesButton {  }
            }
            return
        }

        val regData = HashMap<String, Any>()

        regData.put("id", id)
        regData.put("password", password)
        regData.put("name", name)
        regData.put("phone", phoneNum)
        regData.put("birth", birth)
        regData.put("schoolgrade", stuGrade)
        regData.put("schoolclass", stuClass)

        val agency = SignUpPhoneDropdown

//        val regData = RegData(id, password, name, phoneNum, nickname, birth, stuGrade, stuClass)

        // 가입 구현
        // 닉네임, ID, (학년,반), (통신사, 휴대폰 번호) 중복되지 않을 시
        // & 모든 칸에 빈칸이 없다면

        // 입력받은 회원정보 POST
        (application as MasterApplication).service.signUp(regData)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!.get("success") == "true") {
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    } else {
                        toast("회원가입 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

//    private fun checkNicknameDup() {
//        val nicknameMap = HashMap<String, String>()
//
//        nicknameMap.put("nickname", SignUpNicknameEditTextView.text.toString())
//
//        // 닉네임 중복확인 버튼 기능구현
//        (application as MasterApplication).service.confirmNickname(nicknameMap)
//            .enqueue(object : Callback<HashMap<String, String>> {
//                override fun onResponse(
//                    call: Call<HashMap<String, String>>,
//                    response: Response<HashMap<String, String>>
//                ) {
//                    if (response.isSuccessful) {
//                        if (response.body()!!.get("success") == "true") {
//                            alert("사용불가능한 닉네임입니다.") {
//                                yesButton {  }
//                            }
//                            nicknameConfirm = false
//
//                        } else {
//                            alert("사용가능한 닉네임입니다.") {
//                                yesButton {  }
//                            }
//                            nicknameConfirm = true
//                        }
//                    } else {
//                        toast("error")
//                    }
//                }
//
//                // 응답 실패 시
//                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
//                    toast("network error")
//                    finish()
//                }
//            })
//
//    }

    private fun checkIdDup() {
        val idMap = HashMap<String, String>()

        idMap.put("id", SignUpIdEditTextView.text.toString())

        // ID 중복확인 버튼 기능구현
        (application as MasterApplication).service.confirmId(idMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!.get("success") == "true") {

                        // 중복확인 구현

                    } else {
                        toast("중복확인 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun sendNum() {
        // 인증번호 전송 기능구현
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}