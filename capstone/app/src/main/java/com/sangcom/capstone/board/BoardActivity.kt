package com.sangcom.capstone.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sangcom.capstone.R
import com.sangcom.capstone.adapter.BoardAdapter
import com.sangcom.capstone.dataclass.Post
import com.sangcom.capstone.dataclass.PostList
import com.sangcom.capstone.main.MainActivity
import com.sangcom.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_board.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity : AppCompatActivity() {

    lateinit var type: String
    private lateinit var boardAdapter: BoardAdapter
    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // toolbar 설정
        setSupportActionBar(board_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거
    }

    override fun onResume() {
        super.onResume()
        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type")!!

            if (type == "error") {
                toast("접근 권한이 없습니다")
                finish()
            } else {
                val text = when (type) {
                    "1st_free" -> "1학년 자유게시판"
                    "2nd_free" -> "2학년 자유게시판"
                    "3rd_free" -> "3학년 자유게시판"
                    "sug" -> "학생 건의함"
                    "notice" -> "학생회 공지"
                    "club" -> "동아리 활동"
                    else -> "자유게시판"
                }
                board_toolbar_text.setText(text).toString()

                if (type == "notice") {     // 학생회 공지 게시판일 경우 학생회만 글 작성 가능
                    if ((application as MasterApplication).getUserToken(2) == "student")
                        board_write_btn.visibility = View.GONE
                }
                retrofitGetPostList(false)   // 해당 게시판 전체 게시글 GET
            }
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        board_write_btn.setOnClickListener {
            val intent = Intent(this@BoardActivity, BoardWriteActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("board_write_id", "-1")     // 글 작성의 경우 -1 전달
            intent.putExtra("board_write_title", "-1")
            intent.putExtra("board_write_body", "-1")
            startActivity(intent)
            finish()
        }

        // swipe refresh
        board_swipeRefresh.setOnRefreshListener {
            retrofitGetPostList(true)   // 비동기 필요한듯?
            board_swipeRefresh.isRefreshing = false
        }
    }

    // 게시판 전체 게시글 GET하는 함수
    private fun retrofitGetPostList(swipe: Boolean) {
        (application as MasterApplication).service.getPostList(type)
            .enqueue(object : Callback<PostList> {
                override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        postList = response.body()!!.data

                        if (!swipe) {
                            // 게시판 글 목록 화면 뷰 작성
                            // item 클릭 시 board_id 넘겨줌 + detail 화면으로 전환
                            boardAdapter = BoardAdapter(postList, LayoutInflater.from(this@BoardActivity)) { post ->
                                val intent = Intent(this@BoardActivity, BoardDetailActivity::class.java)
                                intent.putExtra("board_id", post.board_id.toString())
                                intent.putExtra("activity_num", "0")
                                startActivity(intent)
                                finish()
                            }
                            post_recyclerview.adapter = boardAdapter
                            post_recyclerview.layoutManager = LinearLayoutManager(this@BoardActivity)
                            post_recyclerview.setHasFixedSize(true)
                        } else {
                            boardAdapter.refreshPostItem(postList)
                        }
                    } else {
                        toast("게시글 목록을 조회할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<PostList>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // menu xml에서 설정한 menu를 붙임
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.free_board_search -> {
                val intent = Intent(this@BoardActivity, SearchActivity::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
                finish()
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