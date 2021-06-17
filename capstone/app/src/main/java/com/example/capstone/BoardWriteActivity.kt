package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_board_write.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardWriteActivity : AppCompatActivity() {

    // 키보드 InputMethodManager 변수 선언
    var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)

        // toolbar 설정
        setSupportActionBar(board_write_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 글쓰기 완료 버튼을 클릭했을 경우
        board_write_btn.setOnClickListener {
            val title = board_write_title.text.toString()
            val body = board_write_body.text.toString()
            val post = HashMap<String, String>()

            if (title == "") {
                toast("제목을 입력해주세요")
            } else if (body == "") {
                toast("내용을 입력해주세요")
            } else {
                post.put("title", title)
                post.put("body", body)

                // 입력받은 title과 body POST
                retrofitCreatePost(post)
            }
        }

    }

    // 입력받은 title과 body POST하는 함수
    private fun retrofitCreatePost(post: HashMap<String, String>) {
        (application as MasterApplication).service.createPost(post)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!.get("success") == "true") {
                        startActivity(Intent(this@BoardWriteActivity, BoardActivity::class.java))
                    } else {
                        toast("게시글 작성 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    //finish()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, BoardActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}