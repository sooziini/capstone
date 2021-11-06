package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.NotiAdapter
import com.example.capstone.board.BoardDetailActivity
import com.example.capstone.dataclass.NotiList
import com.example.capstone.main.MainActivity
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_notice.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeActivity : AppCompatActivity() {
    private lateinit var notiAdapter: NotiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        // toolbar 설정
        setSupportActionBar(notice_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거
    }

    override fun onResume() {
        super.onResume()

        retrofitGetNotification(false)

        notice_swipeRefresh.setOnRefreshListener {
            retrofitGetNotification(true)
            notice_swipeRefresh.isRefreshing = false
        }
    }

    private fun retrofitGetNotification(swipe: Boolean) {
        (application as MasterApplication).service.getNotification()
            .enqueue(object : Callback<NotiList> {
                override fun onResponse(call: Call<NotiList>, response: Response<NotiList>) {
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val notiList = response.body()!!.data

                        if (!swipe) {
                            notiAdapter = NotiAdapter(notiList, LayoutInflater.from(this@NoticeActivity)) { noti ->
                                val intent = Intent(this@NoticeActivity, BoardDetailActivity::class.java)
                                intent.putExtra("board_id", noti.board_id.toString())
                                intent.putExtra("activity_num", "6")
                                startActivity(intent)
                                finish()
                            }
                            notice_recyclerview.adapter = notiAdapter
                            notice_recyclerview.layoutManager = LinearLayoutManager(this@NoticeActivity)
                            notice_recyclerview.setHasFixedSize(true)
                        } else {
                            notiAdapter.refreshNotiItem(notiList)
                        }
                    } else {
                        toast("알림 목록을 조회할 수 없습니다")
                        finish()
                    }
                }

                override fun onFailure(call: Call<NotiList>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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
}