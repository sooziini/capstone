package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.mancj.materialsearchbar.MaterialSearchBar
import org.jetbrains.anko.toast

class SearchActivity : AppCompatActivity() {

    lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_search)

        val searchBar = findViewById<MaterialSearchBar>(R.id.board_search_bar)
        searchBar.setHint("검색어를 입력하세요")
        searchBar.setSpeechMode(false)      // 음성 검색 모드 OFF

        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
            }

            // 검색 버튼을 클릭했을 경우
            override fun onSearchConfirmed(text: CharSequence?) {
                if (title == "") {
                    toast("검색어를 입력해주세요")
                } else {
                    val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
                    intent.putExtra("title", title)
                    startActivity(intent)
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
            }
        })
    }
}