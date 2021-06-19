package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.PostList
import com.example.capstone.network.MasterApplication
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_board_search.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_search)

        val searchBar = findViewById<MaterialSearchBar>(R.id.board_search_bar)
        searchBar.clearSuggestions()

        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                search_recyclerview.visibility = View.GONE
            }

            // 검색 버튼을 클릭했을 경우
            override fun onSearchConfirmed(text: CharSequence?) {
                if (title == "") {
                    search_recyclerview.visibility = View.GONE
                    toast("검색어를 입력해주세요")
                } else {
                    // 키보드 InputMethodManager 세팅
                    // 버튼 클릭 시 키보드 내리기
                    val imm: InputMethodManager = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(search_recyclerview.windowToken, 0)

                    search_recyclerview.visibility = View.VISIBLE
                    retrofitSearchPostList(title)
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

    private fun retrofitSearchPostList(title: String) {
        (application as MasterApplication).service.searchPostList(title)
            .enqueue(object : Callback<PostList> {
                override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val postList = response.body()!!.data

                        // 검색 결과가 없을 경우
                        if (postList.isEmpty()) {
                            search_recyclerview.visibility = View.GONE
                            toast("검색한 결과가 없습니다")
                        } else {
                            // 검색 결과가 있을 경우
                            // 검색한 게시판 글 목록 화면 뷰 작성
                            // item 클릭 시 board_id 넘겨줌 + detail 화면으로 전환
                            val adapter = BoardAdapter(postList, LayoutInflater.from(this@SearchActivity)) { post ->
                                val intent = Intent(this@SearchActivity, BoardDetailActivity::class.java)
                                intent.putExtra("board_id", post.board_id.toString())
                                startActivity(intent)
                            }
                            search_recyclerview.adapter = adapter
                            search_recyclerview.layoutManager = LinearLayoutManager(this@SearchActivity)
                        }
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<PostList>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }
}