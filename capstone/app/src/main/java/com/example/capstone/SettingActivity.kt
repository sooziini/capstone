package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // toolbar 설정
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        SettingChangeImageImage.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeProfileImageActivity::class.java))
            finish()
        }
        SettingChangeImageText.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangeProfileImageActivity::class.java))
            finish()
        }
        SettingChangePasswordImage.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ChangePasswordActivity::class.java))
            finish()
        }
        SettingChangePasswordText.setOnClickListener{
            startActivity(Intent(this@SettingActivity, ChangePasswordActivity::class.java))
            finish()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}