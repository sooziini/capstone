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
import kotlinx.android.synthetic.main.activity_free_board.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FreeBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_board)

        // toolbar 설정
        setSupportActionBar(free_board_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        free_board_write.setOnClickListener {
            startActivity(Intent(this, BoardWriteActivity::class.java))
        }

        // 자유게시판 전체 게시글 GET
        (application as MasterApplication).service.getPostList()
            .enqueue(object : Callback<PostList> {
                override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val postListList = response.body()
                        val postList = postListList!!.data

                        // 게시판 글 목록 화면 뷰 작성
                        // item 클릭 시 board_id 넘겨줌 + detail 화면으로 전환
                        val adapter = BoardAdapter(postList, LayoutInflater.from(this@FreeBoardActivity)) { post ->
                            val intent = Intent(this@FreeBoardActivity, BoardDetailActivity::class.java)
                            intent.putExtra("board_id", post.board_id.toString())
                            startActivity(intent)
                        }
                        post_recyclerview.adapter = adapter
                        post_recyclerview.layoutManager = LinearLayoutManager(this@FreeBoardActivity)
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
        menuInflater.inflate(R.menu.free_board_menu, menu)
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
                startActivity(Intent(this, SearchActivity::class.java))
                // finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}