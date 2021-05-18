package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.PostList
import kotlinx.android.synthetic.main.activity_free_board.*
import kotlinx.android.synthetic.main.activity_search_result.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        // toolbar 설정
        setSupportActionBar(search_result_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        if (intent.hasExtra("title")) {
            title = intent.getStringExtra("title")!!

            // 받은 검색 단어로 검색한 결과 GET
            (application as MasterApplication).service.searchPostList(title as String)
                .enqueue(object : Callback<PostList> {
                    override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                        // 응답 성공 시
                        if (response.isSuccessful && response.body()!!.success == "true") {
                            val postListList = response.body()
                            val postList = postListList!!.data

                            // 검색 결과가 없을 경우
                            if (postList.isEmpty()) {
                                toast("검색한 결과가 없습니다")
                                //finish()
                            } else {
                                // 검색 결과가 있을 경우
                                // 검색한 게시판 글 목록 화면 뷰 작성
                                // item 클릭 시 board_id 넘겨줌 + detail 화면으로 전환
                                val adapter = BoardAdapter(
                                    postList,
                                    LayoutInflater.from(this@SearchResultActivity)
                                ) { post ->
                                    val intent = Intent(this@SearchResultActivity, BoardDetailActivity::class.java)
                                    intent.putExtra("board_id", post.board_id.toString())
                                    startActivity(intent)
                                }
                                search_recyclerview.adapter = adapter
                                search_recyclerview.layoutManager = LinearLayoutManager(this@SearchResultActivity)
                            }
                        }
                    }

                    // 응답 실패 시
                    override fun onFailure(call: Call<PostList>, t: Throwable) {
                        toast("network error")
                        finish()
                    }
                })
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, SearchActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}