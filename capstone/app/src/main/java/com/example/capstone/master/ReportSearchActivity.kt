package com.example.capstone.master

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.BoardReportAdapter
import com.example.capstone.adapter.ReplyReportAdapter
import com.example.capstone.board.BoardDetailActivity
import com.example.capstone.dataclass.BoardReport
import com.example.capstone.dataclass.ReplyReport
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_report_search.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class ReportSearchActivity : AppCompatActivity() {
    lateinit var title: String
    var reply: Int? = null
    var board: Int? = null
    var boardShow = false
    var replyShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_search)

        report_search_boardbutton.setOnClickListener {
            if(!boardShow) {
                report_search_boardbutton.setImageResource(R.drawable.ic_arrow_up)
                report_search_board_rcv.visibility = View.VISIBLE
            }
            else {
                report_search_boardbutton.setImageResource(R.drawable.ic_arrow_down)
                report_search_board_rcv.visibility = View.GONE
            }
            boardShow = !boardShow
        }

        report_search_replybutton.setOnClickListener {
            if(!replyShow) {
                report_search_replybutton.setImageResource(R.drawable.ic_arrow_up)
                report_search_reply_rcv.visibility = View.VISIBLE
            }
            else {
                report_search_replybutton.setImageResource(R.drawable.ic_arrow_down)
                report_search_reply_rcv.visibility = View.GONE
            }
            replyShow = !replyShow
        }

        val searchBar = findViewById<MaterialSearchBar>(R.id.report_search_bar)
        searchBar.clearSuggestions()

        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                report_search_id_layout.visibility = View.GONE
                report_search_body_scrollview.visibility = View.GONE
            }

            // 검색 버튼을 클릭했을 경우
            override fun onSearchConfirmed(text: CharSequence?) {
                if (title == "") {
                    report_search_id_layout.visibility = View.GONE
                    report_search_body_scrollview.visibility = View.GONE
                    toast("검색어를 입력해주세요")
                } else {
                    // 키보드 InputMethodManager 세팅
                    // 버튼 클릭 시 키보드 내리기
                    val imm: InputMethodManager = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(report_search_board_rcv.windowToken, 0)

                    // 호출
                    report_search_id_text.text = title
                    readBCount(title)
                    readRCount(title)
                    loadBData(title)
                    loadRData(title)
                }
            }

            override fun onButtonClicked(buttonCode: Int) {
            }
        })

        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            // 검색어 변경될 때마다 title에 저장
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // 검색어 자동 저장 기능 OFF
                searchBar.clearSuggestions()
            }
        })
    }

    // 게시판 신고 횟수 조회
    private fun readBCount(id: String) {
        board = null
        (application as MasterApplication).service.readBoardReportCount()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val array = response.body()!!["count"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (item in array) {
                            if(item["recv_id"] as String == id) {
                                board = (item["count"] as Double).roundToInt()
                                break
                            }
                        }
                        if (board == null) board = 0

                        setCount()
                    } else {
                        toast("조회 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 댓글 신고 횟수 조회
    private fun readRCount(id: String) {
        reply = null
        (application as MasterApplication).service.readReplyReportCount()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val array = response.body()!!["count"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (item in array) {
                            if(item["recv_id"] as String == id) {
                                reply = (item["count"] as Double).roundToInt()
                                break
                            }
                        }
                        if (reply == null) reply = 0

                        setCount()
                    } else {
                        toast("조회 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // count 세팅
    private fun setCount() {
        if (reply == null || board == null)
            return

        val count = reply!! + board!!
        if (count == 0) {
            report_search_id_layout.visibility = View.GONE
            report_search_body_scrollview.visibility = View.GONE
            toast("검색 결과가 없습니다.")
        }
        else {
            report_search_number.text = count.toString()

            report_search_id_layout.visibility = View.VISIBLE
            report_search_body_scrollview.visibility = View.VISIBLE
        }
    }

    private fun loadBData(id: String) {
        val reportList = ArrayList<BoardReport>()

        (application as MasterApplication).service.masterLoadBoardReport(id)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val array = response.body()!!["board"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (item in array) {
                            val board_id = (item["board_id"] as Double).roundToInt()
                            val send_id = item["send_id"] as String
                            val recv_id = item["recv_id"] as String
                            val body = item["body"] as String
                            val regDate = item["regdate"] as String

                            reportList.add(BoardReport(board_id, send_id, recv_id, body, regDate))
                        }

                        report_search_board_rcv.adapter = BoardReportAdapter(reportList, LayoutInflater.from(this@ReportSearchActivity),"master") { boardId ->
                            val intent = Intent(this@ReportSearchActivity, BoardDetailActivity::class.java)
                                .putExtra("board_id", boardId)
                                .putExtra("activity_num", "4")
                                .putExtra("masterRole", true)
                            startActivity(intent)
                            finish()
                        }
                        report_search_board_rcv.layoutManager = LinearLayoutManager(this@ReportSearchActivity)
                        report_search_board_rcv.setHasFixedSize(true)
                    } else {
                        toast("조회 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun loadRData(id: String) {
        val reportList = ArrayList<ReplyReport>()

        (application as MasterApplication).service.masterLoadReplyReport(id)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val array = response.body()!!["reply"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (item in array) {
                            val reply_id = (item["reply_id"] as Double).roundToInt()
                            val board_id: Int = (item["board_id"] as Double).roundToInt()
                            val send_id = item["send_id"] as String
                            val recv_id = item["recv_id"] as String
                            val body = item["body"] as String
                            val regDate = item["regdate"] as String

                            reportList.add(ReplyReport(reply_id, board_id, send_id, recv_id, body, regDate))
                        }

                        report_search_reply_rcv.adapter = ReplyReportAdapter(reportList, LayoutInflater.from(this@ReportSearchActivity), "master") { boardId ->
                            val intent = Intent(this@ReportSearchActivity, BoardDetailActivity::class.java)
                                .putExtra("board_id", boardId)
                                .putExtra("activity_num", "4")
                                .putExtra("masterRole", true)
                            startActivity(intent)
                            finish()
                        }
                        report_search_reply_rcv.layoutManager = LinearLayoutManager(this@ReportSearchActivity)
                        report_search_reply_rcv.setHasFixedSize(true)
                    } else {
                        toast("조회 실패")
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