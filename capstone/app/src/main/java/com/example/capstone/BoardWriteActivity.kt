package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_board_write.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardWriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)

        board_write_back.setOnClickListener {
            startActivity(Intent(this, FreeBoardActivity::class.java))
        }

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
                (application as MasterApplication).service.createPost(post)
                    .enqueue(object : Callback<HashMap<String, String>> {
                        override fun onResponse(
                            call: Call<HashMap<String, String>>,
                            response: Response<HashMap<String, String>>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result!!.get("success") == "true") {
                                    startActivity(Intent(this@BoardWriteActivity, FreeBoardActivity::class.java))
                                } else {
                                    toast("글 작성 실패")
                                }
                            } else {
                                toast("error")
                            }
                        }

                        // 응답 실패 시
                        override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                            toast("network error")
                        }
                    })
            }
        }

    }
}