package com.example.capstone.master

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
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
import com.example.capstone.dataclass.Reply
import com.example.capstone.user.LoginActivity

class MasterReportActivity : AppCompatActivity() {
    private var boardShow = false
    private var replyShow = false
    private val boardReportList = ArrayList<BoardReport>()
    private val replyReportList = ArrayList<ReplyReport>()
    private var boardPage = 1
    private var replyPage = 1
    private var boardNoData = false
    private var replyNoData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_report)

        // toolbar 설정
        setSupportActionBar(master_report_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        readBoardReport()
        readReplyReport()

        setBoardPage(boardPage)
        setReplyPage(replyPage)

        // 게시판 신고 이전 버튼
        BoardReportPreviewButton.setOnClickListener {
            if (boardPage != 1) {
                boardPage -= 1
                boardNoData = false
            }
            else if (boardPage == 1)
                return@setOnClickListener

            readBoardReport()
        }

        // 게시판 신고 다음 버튼
        BoardReportNextButton.setOnClickListener {
            if (boardPage == 1 && boardNoData) {
                toast("마지막 페이지입니다.")
                return@setOnClickListener
            }
            boardPage += 1

            readBoardReport()
        }

        // 댓글 신고 이전 버튼
        ReplyReportPreviewButton.setOnClickListener {
            if(replyPage != 1) {
                replyPage -= 1
                replyNoData = false
            }
            else if (replyPage == 1)
                return@setOnClickListener

            readReplyReport()
        }

        // 댓글 신고 다음 버튼
        ReplyReportNextButton.setOnClickListener {
            if (replyPage == 1 && replyNoData) {
                toast("마지막 페이지입니다.")
                return@setOnClickListener
            }
            replyPage += 1

            readReplyReport()
        }

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
            R.id.report_search -> {
                startActivity(Intent(this, ReportSearchActivity::class.java))
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
        (application as MasterApplication).service.readBoardReport(boardPage)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val reportArray = response.body()!!["board"] as ArrayList<LinkedTreeMap<String, Any>>

                        if(reportArray.size == 0) {
                            if (boardPage != 1) {
                                boardPage -= 1
                                toast("마지막 페이지입니다.")
                            }
                            else if (boardPage == 1)
                                boardNoData = true
                            return
                        }

                        if (!boardNoData) setBoardPage(boardPage)

                        boardReportList.clear()

                        for (item in reportArray) {
                            val board_id = (item["board_id"] as Double).roundToInt()
                            val send_id = item["send_id"] as String
                            val recv_id = item["recv_id"] as String
                            val body = item["body"] as String
                            val regDate = item["regdate"] as String
                            boardReportList.add(BoardReport(board_id, send_id, recv_id, body, regDate))
                        }

                        BoardReportrcv.adapter = BoardReportAdapter(boardReportList, LayoutInflater.from(this@MasterReportActivity))
                        BoardReportrcv.layoutManager = LinearLayoutManager(this@MasterReportActivity)
                        BoardReportrcv.setHasFixedSize(true)
                    } else {
                        toast("로드 실패")
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

        (application as MasterApplication).service.readReplyReport(replyPage)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val reportArray = response.body()!!["reply"] as ArrayList<LinkedTreeMap<String, Any>>

                        if(reportArray.size == 0) {
                            if (replyPage != 1) {
                                replyPage -= 1
                                toast("마지막 페이지입니다.")
                            }
                            else if (replyPage == 1)
                                replyNoData = true
                            return
                        }

                        if (!replyNoData) setReplyPage(replyPage)

                        replyReportList.clear()

                        for (item in reportArray) {
                            val reply_id = (item["reply_id"] as Double).roundToInt()
                            val send_id = item["send_id"] as String
                            val recv_id = item["recv_id"] as String
                            val body = item["body"] as String
                            val regDate = item["regdate"] as String
                            replyReportList.add(ReplyReport(reply_id, send_id, recv_id, body, regDate))
                        }

                        ReplyReportrcv.adapter = ReplyReportAdapter(replyReportList, LayoutInflater.from(this@MasterReportActivity))
                        ReplyReportrcv.layoutManager = LinearLayoutManager(this@MasterReportActivity)
                        ReplyReportrcv.setHasFixedSize(true)
                    } else {
                        toast("로드 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun setBoardPage(page: Int) {
        BoardReportPage.text = "$page 페이지"
    }

    private fun setReplyPage(page: Int) {
        ReplyReportPage.text = "$page 페이지"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.master_report_menu, menu)
        return true
    }
}