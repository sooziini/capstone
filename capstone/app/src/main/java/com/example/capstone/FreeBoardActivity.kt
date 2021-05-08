package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.Post
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

        (application as MasterApplication).service.getPostList()
            .enqueue(object : Callback<PostList> {
                override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                    // 응답 성공 시
                    if (response.isSuccessful) {
                        toast("post success")

                        val postListList = response.body()
                        val postList = postListList!!.data
                        Log.d("test", ""+response.raw())

                        // 게시판 글 목록 화면 뷰 작성
                        val adapter = BoardAdapter(postList, LayoutInflater.from(this@FreeBoardActivity))
                        post_recyclerview.adapter = adapter
                        post_recyclerview.layoutManager = LinearLayoutManager(this@FreeBoardActivity)
                    } else {
                        toast("post fail")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<PostList>, t: Throwable) {
                    toast("post fail")
                }
            })
    }
}