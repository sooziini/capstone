package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.Post
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_scrap.*
import org.jetbrains.anko.toast

class CommentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

//        val sampleCommentList : ArrayList<Post> = arrayListOf(
//            Post(1,"board1","text1","user1","reg1",0),
//            Post(2,"board2","text2","user2","reg2",0),
//            Post(3,"board3","text3","user3","reg3",0),
//            Post(4,"board4","text4","user4","reg4",0),
//        )
//
//        rv_commentBoard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
//        rv_commentBoard.setHasFixedSize(true)
//
//
//        rv_commentBoard.adapter = BoardAdapter(sampleCommentList,layoutInflater,itemClick = {post ->
//            toast("${post.title},${post.body}")
//        })
    }
}