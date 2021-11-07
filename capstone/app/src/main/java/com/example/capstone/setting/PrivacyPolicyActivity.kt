package com.example.capstone.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.capstone.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.activity_setting.*

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        // toolbar 설정
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 개인정보처리방침 txt 파일 로드
        val inn = resources.openRawResource(R.raw.privacypolicy)
        val b: ByteArray = inn.readBytes()
        val privacy = String(b)
        privacyPolicy.text = privacy
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SettingActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {    // toolbar의 뒤로가기 버튼을 눌렀을 때
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}