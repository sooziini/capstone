package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.PostList
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_board.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity : AppCompatActivity() {

    lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // toolbar 설정
        setSupportActionBar(board_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type")!!

            // 해당 게시판 전체 게시글 GET
            retrofitGetPostList(type)
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
        }
    }

    // 게시판 전체 게시글 GET하는 함수
    private fun retrofitGetPostList(type: String) {
        (application as MasterApplication).service.getPostList(type)
            .enqueue(object : Callback<PostList> {
                override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val postList = response.body()!!.data

                        // 게시판 글 목록 화면 뷰 작성
                        // item 클릭 시 board_id 넘겨줌 + detail 화면으로 전환
                        val adapter = BoardAdapter(postList, LayoutInflater.from(this@BoardActivity)) { post ->
                            val intent = Intent(this@BoardActivity, BoardDetailActivity::class.java)
                            intent.putExtra("type", type)
                            intent.putExtra("board_id", post.board_id.toString())
                            intent.putExtra("activity_num", "0")
                            startActivity(intent)
                        }
                        post_recyclerview.adapter = adapter
                        post_recyclerview.layoutManager = LinearLayoutManager(this@BoardActivity)
                        post_recyclerview.setHasFixedSize(true)
                    } else {
                        toast("게시글 목록 조회 실패")
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
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.free_board_search -> {
                val intent = Intent(this@BoardActivity, SearchActivity::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
                //finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}