package com.example.capstone.master

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardReportAdapter
import com.example.capstone.adapter.ReplyReportAdapter
import com.example.capstone.dataclass.BoardReport
import com.example.capstone.dataclass.ReplyReport
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_master_report.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt
import com.example.capstone.R

class MasterReportActivity : AppCompatActivity() {
    private var boardShow = false
    private var replyShow = false
    private val boardReportList = ArrayList<BoardReport>()
    private val replyReportList = ArrayList<ReplyReport>()
    private var boardPage = 1
    private var replyPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_report)

        // toolbar 설정
        setSupportActionBar(master_report_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        readBoardReport()
        readReplyReport()

        BoardReportrcv.adapter = BoardReportAdapter(boardReportList, LayoutInflater.from(this))
        BoardReportrcv.layoutManager = LinearLayoutManager(this)
        BoardReportrcv.setHasFixedSize(true)

        ReplyReportrcv.adapter = ReplyReportAdapter(replyReportList, LayoutInflater.from(this))
        ReplyReportrcv.layoutManager = LinearLayoutManager(this)
        ReplyReportrcv.setHasFixedSize(true)

        BoardReportButton.setOnClickListener {
            if(!boardShow) {
                BoardReportButton.setImageResource(R.drawable.ic_arrow_up)
                BoardReportButtonLayout.visibility = View.VISIBLE
                BoardReportrcv.visibility = View.VISIBLE
            } else {
                BoardReportButton.setImageResource(R.drawable.ic_arrow_down)
                BoardReportButtonLayout.visibility = View.GONE
                BoardReportrcv.visibility = View.GONE
            }
            boardShow = !boardShow
        }

        ReplyReportButton.setOnClickListener {
            if(!replyShow) {
                ReplyReportButton.setImageResource(R.drawable.ic_arrow_up)
                ReplyReportButtonLayout.visibility = View. VISIBLE
                ReplyReportrcv.visibility = View.VISIBLE
            } else {
                ReplyReportButton.setImageResource(R.drawable.ic_arrow_down)
                ReplyReportButtonLayout.visibility = View.GONE
                ReplyReportrcv.visibility = View.GONE
            }
            replyShow = !replyShow
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
        startActivity(Intent(this, MainActivity2::class.java))
        finish()
    }

    private fun readBoardReport() {
        boardReportList.clear()

        (application as MasterApplication).service.readBoardReport(boardPage)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val reportArray = response.body()!!["board"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (item in reportArray) {
                            val board_id = (item["board_id"] as Double).roundToInt()
                            val send_id = item["send_id"] as String
                            val recv_id = item["recv_id"] as String
                            val body = item["body"] as String
                            val regDate = item["regdate"] as String
                            boardReportList.add(BoardReport(board_id, send_id, recv_id, body, regDate))
                        }
                    } else {
                        toast("로그아웃 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun readReplyReport() {
        replyReportList.clear()

        (application as MasterApplication).service.readReplyReport(replyPage)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val reportArray = response.body()!!["reply"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (item in reportArray) {
                            val reply_id = (item["reply_id"] as Double).roundToInt()
                            val send_id = item["send_id"] as String
                            val recv_id = item["recv_id"] as String
                            val body = item["body"] as String
                            val regDate = item["regdate"] as String
                            replyReportList.add(ReplyReport(reply_id, send_id, recv_id, body, regDate))
                        }
                    } else {
                        toast("로그아웃 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }
}