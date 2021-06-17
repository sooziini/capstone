package com.example.capstone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.*
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.PostList
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 자유게시판 전체 게시글 GET
        (activity?.application as MasterApplication).service.getPostList()
            .enqueue(object : Callback<PostList> {
                override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                    // 응답 성공 시
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val postListList = response.body()
                        val postList = postListList!!.data

                        // 게시판 글 목록 화면 뷰 작성
                        // item 클릭 시 board_id 넘겨줌 + detail 화면으로 전환
                        val adapter = BoardAdapter(postList, LayoutInflater.from(context)) { post ->
                            activity?.let {
                                val intent = Intent(context, BoardDetailActivity::class.java)
                                    .putExtra("board_id", post.board_id.toString())
                                startActivity(intent)
                            }
                        }
                        home_recyclerview.adapter = adapter
                        home_recyclerview.layoutManager = LinearLayoutManager(context)
                    } else {
                        toast("게시글 목록 조회 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<PostList>, t: Throwable) {
                    toast("network error")
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.remove(this@HomeFragment)
                        ?.commit()
                }
            })

        MainLunchTableButton.setOnClickListener {
            // 추후 구현
        }

        MainFreeBoardButton.setOnClickListener {
            activity?.let{
                // startActivity<FreeBoardActivity>()
                val intent = Intent(context, BoardActivity::class.java)
                startActivity(intent)
            }
        }

    }

}