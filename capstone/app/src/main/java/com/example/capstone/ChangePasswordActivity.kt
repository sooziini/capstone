package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    // 키보드 InputMethodManager 변수 선언
    private var imm: InputMethodManager? = null
    lateinit var intentUserId: String
    lateinit var intentUserName: String
    lateinit var intentUserStudentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // toolbar 설정
        setSupportActionBar(changepassword_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거
    }

    override fun onResume() {
        super.onResume()

        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("user_id")) {
            intentUserId = intent.getStringExtra("user_id")!!
            intentUserName = intent.getStringExtra("user_name")!!
            intentUserStudentId = intent.getStringExtra("user_student_id")!!
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        ChangePasswordAuthButton.setOnClickListener {
            authPassword()
        }

        ChangePasswordButton.setOnClickListener {
            changePassword()
        }
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

    override fun onBackPressed() {
        val intent = Intent(this, SettingActivity::class.java)
        intent.putExtra("user_id", intentUserId)
        intent.putExtra("user_name", intentUserName)
        intent.putExtra("user_student_id", intentUserStudentId)
        startActivity(intent)
        finish()
    }

    private fun authPassword() {
        val map = HashMap<String, String>()
        val present = ChangePasswordPresentEditText.text.toString()

        map["password"] = present

        (application as MasterApplication).service.checkPassword(map)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        ChangePasswordLayout.visibility = View.VISIBLE
                        ChangePasswordButton.visibility = View.VISIBLE
                        toast("비밀번호가 확인되었습니다")
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("비밀번호가 일치하지 않습니다")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun changePassword() {
        val map = HashMap<String, String>()
        val pass1 = ChangePasswordEditText.text.toString()
        val pass2 = ChangePasswordAuthEditText.text.toString()

        if (pass1 != pass2){
            toast("입력한 비밀번호를 확인해주세요")
            return
        }

        map["password"] = pass1

        (application as MasterApplication).service.changePassword(map)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        toast("비밀번호가 변경되었습니다")
                        startActivity(Intent(this@ChangePasswordActivity, MainActivity::class.java))
                        finish()
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("비밀번호 변경에 실패했습니다")
                    }
                }

                // 응답 실패 시
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
}