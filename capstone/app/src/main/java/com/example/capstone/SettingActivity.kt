package com.example.capstone

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {

    lateinit var intentUserId: String
    lateinit var intentUserName: String
    lateinit var intentUserStudentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // toolbar 설정
        setSupportActionBar(setting_toolbar)
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

            setting_name.setText(intentUserName).toString()
            setting_id.setText(intentUserId).toString()

            val userGrade = intentUserStudentId.substring(0, 1).toInt().toString()
            val userClass = intentUserStudentId.substring(1, 3).toInt().toString()
            val userNumber = intentUserStudentId.substring(3, 5).toInt().toString()
            setting_student_id1.setText(userGrade).toString()
            setting_student_id2.setText(userClass).toString()
            setting_student_id3.setText(userNumber).toString()
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        // 프로필 사진 변경
        SettingChangeProfileLayout.setOnClickListener {

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
            setUserDeleteDialog()
        }

    }

    private fun setUserDeleteDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "회원탈퇴 하시겠습니까?"

        builder.setPositiveButton("확인") { dialog, it ->
            (application as MasterApplication).service.deleteUser()
                .enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse(
                        call: Call<HashMap<String, String>>,
                        response: Response<HashMap<String, String>>
                    ) {
                        if (response.isSuccessful) {
                            toast("회원탈퇴가 완료되었습니다")
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
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
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