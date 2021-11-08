package com.sangcom.capstone.master

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sangcom.capstone.user.LoginActivity
import com.sangcom.capstone.R
import com.sangcom.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity2 : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private var mBackWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // toolbar 설정
        setSupportActionBar(main2_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 학생 조회
        Main2_ReadStudentLayout.setOnClickListener {
            startActivity(Intent(this, StudentListActivity::class.java))
            finish()
        }

        // 학생파일 등록
        Main2_EnrollStudentLayout.setOnClickListener {
            val permissionChk = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionChk != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE)
                return@setOnClickListener
            }

            // 권한이 있는 경우
            startActivity(Intent(this, EnrollStudentActivity::class.java))
            finish()
        }

        // 신고 조회
        Main2_ReadWarningLayout.setOnClickListener {
            startActivity(Intent(this, MasterReportActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main2_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.main2_menu_logout -> {
                setLogoutDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 로그아웃 dialog 설정 함수
    private fun setLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "로그아웃 하시겠습니까?"

        builder.setPositiveButton("확인") { _, _ ->
            retrofitLogout()
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    // 로그아웃하는 함수
    private fun retrofitLogout() {
        val app = application as MasterApplication
        app.service.logout()
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        app.deleteUserInfo()
                        app.createRetrofit(null)
                        startActivity(Intent(this@MainActivity2, LoginActivity::class.java))
                        finish()
                        toast("로그아웃 되었습니다")
                    } else {
                        toast("로그아웃을 할 수 없습니다")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - mBackWait >= 2000 ) {
            mBackWait = System.currentTimeMillis()
            toast("뒤로가기 버튼을 한번 더 누르면 종료됩니다")
        } else {
            finish()
        }
    }
}