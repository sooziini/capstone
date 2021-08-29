package com.example.capstone

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // toolbar 설정
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 프로필 사진 변경
        SettingChangeImageLayout.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeProfileImageActivity::class.java))
            finish()
        }

        // 비밀번호 변경
        SettingChangePasswordLayout.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangePasswordActivity::class.java))
            finish()
        }

        // 내 정보
        SettingMyInfoLayout.setOnClickListener {
            startActivity((Intent(this, MyInfoActivity::class.java)))
            finish()
        }

        // 회원탈퇴 미구현
        SettinguserDeleteLayout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원탈퇴")
            builder.setMessage("회원탈퇴 하시겠습니까?")
            builder.setIcon(R.drawable.ic_personoff)

            val listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            (application as MasterApplication).service.deleteUser()
                                .enqueue(object : Callback<HashMap<String, String>> {
                                    override fun onResponse(
                                        call: Call<HashMap<String, String>>,
                                        response: Response<HashMap<String, String>>
                                    ) {
                                        if (response.isSuccessful) {
                                            toast("회원탈퇴가 완료되었습니다.")
                                            startActivity((Intent(this@SettingActivity, LoginActivity::class.java)))
                                            finish()
                                        } else {        // 3xx, 4xx 를 받은 경우
                                            toast("회원탈퇴 실패")
                                        }
                                    }

                                    // 응답 실패 시
                                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                                        toast("network error")
                                        finish()
                                    }
                                })
                        }
                        DialogInterface.BUTTON_NEGATIVE ->
                            return
                    }
                }
            }
            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)

            builder.show()
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}