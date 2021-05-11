package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_board_detail.*
import kotlinx.android.synthetic.main.activity_board_write.*
import org.jetbrains.anko.toast

class BoardDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)

        // toolbar 설정
        setSupportActionBar(board_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("board_id")) {
            val board_id = intent.getStringExtra("board_id")
            toast(board_id)
        } else {
            finish()
        }

    }

    // menu xml에서 설정한 menu를 붙임
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, FreeBoardActivity::class.java))
                finish()
                return true
            }
            R.id.board_detail_edit -> {
                toast("edit success")
                // view 필요
                return true
            }
            R.id.board_detail_remove -> {
                toast("remove success")
                // view 필요
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}