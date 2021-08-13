package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_find_password.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPasswordActivity : AppCompatActivity() {
    // 키보드 InputMethodManager 변수 선언
    var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        // toolbar 설정
        setSupportActionBar(FindPasswordToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        FindPasswordCommitbutton.setOnClickListener {
            sendTempPassword()      // 임시 비밀번호 전송
        }
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

    private fun sendTempPassword() {
        val id = FindPasswordIdEditText.text.toString()
        val name = FindPasswordNameEditText.text.toString()
        val stuId = FindPasswordStuIdEditText.text.toString()
        val map = HashMap<String, String>()

        if (id.length == 0 || name.length == 0 || stuId.length == 0) {
            toast("빈칸 없이 입력해주세요")
            return
        }

        map.put("id", id)
        map.put("name", name)
        map.put("studentId", stuId)

        (application as MasterApplication).service.findPassword(map)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        toast("임시 비밀번호가 발급되었습니다.")
                        startActivity(Intent(this@FindPasswordActivity, LoginActivity::class.java))
                        finish()
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("입력정보를 확인해주세요.")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }
}