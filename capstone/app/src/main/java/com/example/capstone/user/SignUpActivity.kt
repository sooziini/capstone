package com.example.capstone.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.capstone.R
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    // 키보드 InputMethodManager 변수 선언
    private var imm: InputMethodManager? = null
    var idConfirm: Boolean = false
    var stuAuth = false

    inner class IdEditWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            idConfirm = false
            SignUpIdEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                R.color.warn_red
            )
        }
    }

    inner class NameEditWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            stuAuth = false
            SignUpNameEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                R.color.warn_red
            )
        }
    }

    inner class YearEditWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            stuAuth = false
            SignUpYearEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                R.color.warn_red
            )
        }
    }

    inner class StuNumEditWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun afterTextChanged(s: Editable?) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            stuAuth = false
            SignUpStuNumEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                R.color.warn_red
            )
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
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { }
        }

        // 반 드롭다운 스피너
        val classList = arrayOf("반", "1", "2", "3", "4", "5", "6", "7", "8")
        SignUpClassDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classList)
        SignUpClassDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { }
        }

        // ID 중복확인 버튼
        SignUpIdButton.setOnClickListener {
            checkIdDup()
        }

        // 회원가입 버튼
        SignUpButton.setOnClickListener {
            signUp()        // 가입 메소드
        }

        // 학번인증 버튼
        SignUpAuthButton.setOnClickListener {
            auth()
        }
    }

    private fun signUp() {
        val id = SignUpIdEditTextView.text.toString()
        val password = SignUpPasswordEditTextView.text.toString()
        val passwordconfirm = SignUpPassWordCheckEditTextView.text.toString()
        val name = SignUpNameEditTextView.text.toString()
        val phoneNum = SignUpPhoneEditText.text.toString()
        val birth = SignUpBirthEditText.text.toString()
        val stuGrade: Int
        val stuClass: Int
        val stuNum: Int
        val stuYear: Int
        val email = SignUpEmailEditText.text.toString()

        if (!idConfirm) {
            toast("아이디 중복확인을 해 주세요")
            return
        }

        if(password != passwordconfirm || password == "") {
            toast("비밀번호를 확인해 주세요")
            return
        }

        if (SignUpGradeDropdown.selectedItem.toString() != "학년") {
            stuGrade = SignUpGradeDropdown.selectedItem.toString().toInt()
        }
        else {
            toast("학년을 선택해 주세요")
            return
        }

        if (SignUpClassDropdown.selectedItem.toString() != "반") {
            stuClass = SignUpClassDropdown.selectedItem.toString().toInt()
        }
        else {
            toast("반을 선택해 주세요")
            return
        }

        if (SignUpStuNumEditText.text.isNotEmpty()){
            stuNum = SignUpStuNumEditText.text.toString().toInt()
        }
        else {
            toast("번호를 입력해 주세요")
            return
        }

        if (SignUpYearEditText.text.isNotEmpty()) {
            stuYear = SignUpYearEditText.text.toString().toInt()
        }
        else {
            toast("입학년도를 입력해 주세요")
            return
        }

        if (!stuAuth) {
            toast("학번인증을 해 주세요")
            return
        }
        if(SignUpEmailEditText.text.isEmpty()) {
            toast("이메일을 입력해 주세요")
            return
        }

        // 모든 칸에 빈칸이 없다면
        if (name == "" || phoneNum == "" || birth == "" || email == "") {
            toast("빈칸 없이 입력해 주세요")
            return
        }

        val regData = HashMap<String, Any>()

        regData["id"] = id
        regData["password"] = password
        regData["name"] = name
        regData["phone"] = phoneNum
        regData["birth"] = birth
        regData["schoolgrade"] = stuGrade
        regData["schoolclass"] = stuClass
        regData["schoolnumber"] = stuNum
        regData["role"] = "student"
        regData["year"] = stuYear
        regData["email"] = email

        // 입력받은 회원정보 POST
        (application as MasterApplication).service.signUp(regData)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    } else {
                        toast("회원가입을 할 수 없습니다")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 아이디 중복확인
    private fun checkIdDup() {
        val idMap = HashMap<String, String>()
        val id = SignUpIdEditTextView.text.toString()

        if (id != "") {
            idMap["id"] = id
        } else {
            toast("아이디를 입력해주세요")
            return
        }

        // ID 중복확인 버튼 기능구현
        (application as MasterApplication).service.confirmId(idMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        if(response.body()!!["success"] == "true") {
                            idConfirm = false
                            toast("사용할 수 없는 아이디 입니다")
                            SignUpIdEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.warn_red
                            )
                        } else {
                            idConfirm = true
                            toast("사용할 수 있는 아이디 입니다")
                            SignUpIdEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.colorPrimary
                            )
                            val idWatcher = IdEditWatcher()
                            SignUpIdEditTextView.addTextChangedListener(idWatcher)
                        }
                    } else {
                        toast("중복확인을 할 수 없습니다")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 학번 인증
    private fun auth() {
        val authMap = HashMap<String, String>()
        val name = SignUpNameEditTextView.text.toString()
        val year = SignUpYearEditText.text.toString()
        val stuGrade = SignUpGradeDropdown.selectedItem.toString()
        var stuClass = SignUpClassDropdown.selectedItem.toString()
        var stuNum = SignUpStuNumEditText.text.toString()

        if (stuClass.length == 1) {
            stuClass = "0$stuClass"
        }
        if (stuNum.length == 1) {
            stuNum = "0$stuNum"
        }

        val authNum = year + stuGrade + stuClass + stuNum

        authMap["name"] = name
        authMap["studentId"] = authNum

        (application as MasterApplication).service.authStudent(authMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        if(response.body()!!["success"] == "true") {
                            SignUpNameEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.colorPrimary
                            )
                            SignUpYearEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.colorPrimary
                            )
                            SignUpStuNumEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.colorPrimary
                            )
                            stuAuth = true
                            toast("학생인증 성공")
                            val nameWatcher = NameEditWatcher()
                            SignUpNameEditTextView.addTextChangedListener(nameWatcher)

                            val yearEditWatcher = YearEditWatcher()
                            SignUpYearEditText.addTextChangedListener(yearEditWatcher)

                            val stuNumEditWatcher = StuNumEditWatcher()
                            SignUpStuNumEditText.addTextChangedListener(stuNumEditWatcher)
                        } else {
                            SignUpNameEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.warn_red
                            )
                            SignUpYearEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.warn_red
                            )
                            SignUpStuNumEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                                R.color.warn_red
                            )
                            stuAuth = false
                            toast("학생인증 실패\n이름, 학년, 반, 번호, 입학년도를 확인해 주세요")
                        }
                    } else {
                        SignUpNameEditTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                            R.color.warn_red
                        )
                        SignUpYearEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                            R.color.warn_red
                        )
                        SignUpStuNumEditText.backgroundTintList = ContextCompat.getColorStateList(applicationContext,
                            R.color.warn_red
                        )
                        stuAuth = false
                        toast("학생인증 실패\n이름, 학년, 반, 번호, 입학년도를 확인해 주세요")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
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

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}