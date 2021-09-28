package com.example.capstone.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.BoardReportAdapter
import com.example.capstone.adapter.ReplyReportAdapter
import com.example.capstone.board.BoardDetailActivity
import com.example.capstone.dataclass.BoardReport
import com.example.capstone.dataclass.ReplyReport
import com.example.capstone.main.MainActivity
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_report.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class ReportActivity : AppCompatActivity() {
    lateinit var type: String
    private val boardReportList = ArrayList<BoardReport>()
    private val replyReportList = ArrayList<ReplyReport>()
    private lateinit var intentUserId: String
    private lateinit var intentUserName: String
    private lateinit var intentUserStudentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // toolbar 설정
        setSupportActionBar(report_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거
    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("user_id")) {
            intentUserId = intent.getStringExtra("user_id")!!
            intentUserName = intent.getStringExtra("user_name")!!
            intentUserStudentId = intent.getStringExtra("user_student_id")!!
        }

        if (intent.hasExtra("type"))
            type = intent.getStringExtra("type")!!
        else finish()

        val text = when(type) {
            "board" -> {
                getBoardReportList()
                "게시판 신고목록 조회"
            }
            "reply" -> {
                getReplyReportList()
                "댓글 신고목록 조회"
            }
            else -> {
                finish()
                ""
            }
        }

        report_toolbar_text.text = text
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

    private fun getBoardReportList() {
        (application as MasterApplication).service.userLoadBoardReport()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(call: Call<HashMap<String, Any>>, response: Response<HashMap<String, Any>>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val array = response.body()!!["report"] as ArrayList<LinkedTreeMap<String, String>>

                        for (item in array) {
                            val boardId: Int = (item["board_id"] as Double).roundToInt()
                            val sendId: String = item["send_id"] as String
                            val recvId: String = item["recv_id"] as String
                            val body: String = item["body"] as String
                            val regDate: String = item["regdate"] as String

                            boardReportList.add(BoardReport(boardId, sendId, recvId, body, regDate))
                        }

                        report_activity_rcv.adapter = BoardReportAdapter(boardReportList, LayoutInflater.from(this@ReportActivity), "student") { boardId ->
                            val intent = Intent(this@ReportActivity, BoardDetailActivity::class.java)
                                .putExtra("board_id", boardId)
                                .putExtra("activity_num", "5")
                                .putExtra("user_id", intentUserId)
                                .putExtra("user_name", intentUserName)
                                .putExtra("user_student_id", intentUserStudentId)
                                .putExtra("type", type)
                            startActivity(intent)
                            finish()
                        }
                        report_activity_rcv.layoutManager = LinearLayoutManager(this@ReportActivity)
                        report_activity_rcv.setHasFixedSize(true)
                    } else {
                        toast("게시글 신고목록을 조회할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun getReplyReportList() {
        (application as MasterApplication).service.userLoadReplyReport()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(call: Call<HashMap<String, Any>>, response: Response<HashMap<String, Any>>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val array = response.body()!!["report"] as ArrayList<LinkedTreeMap<String, String>>

                        for (item in array) {
                            val replyId: Int = (item["reply_id"] as Double).roundToInt()
                            val board_id: Int = (item["board_id"] as Double).roundToInt()
                            val sendId: String = item["send_id"] as String
                            val recvId: String = item["recv_id"] as String
                            val body: String = item["body"] as String
                            val regDate: String = item["regdate"] as String

                            replyReportList.add(ReplyReport(replyId, board_id, sendId, recvId, body, regDate))
                        }

                        report_activity_rcv.adapter = ReplyReportAdapter(replyReportList, LayoutInflater.from(this@ReportActivity), "student") { boardId ->
                            val intent = Intent(this@ReportActivity, BoardDetailActivity::class.java)
                                .putExtra("board_id", boardId)
                                .putExtra("activity_num", "5")
                                .putExtra("user_id", intentUserId)
                                .putExtra("user_name", intentUserName)
                                .putExtra("user_student_id", intentUserStudentId)
                                .putExtra("type", type)
                            startActivity(intent)
                            finish()
                        }
                        report_activity_rcv.layoutManager = LinearLayoutManager(this@ReportActivity)
                        report_activity_rcv.setHasFixedSize(true)
                    } else {
                        toast("댓글 신고목록을 조회할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onBackPressed() {
        val intent = Intent(this, SettingActivity::class.java)
        intent.putExtra("user_id", intentUserId)
        intent.putExtra("user_name", intentUserName)
        intent.putExtra("user_student_id", intentUserStudentId)
        startActivity(intent)
        finish()
    }
}