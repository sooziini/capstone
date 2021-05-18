package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.dataclass.Post
import kotlinx.android.synthetic.main.activity_scrap.*
import org.jetbrains.anko.toast

class ScrapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrap)

        val sampleScrapList : ArrayList<Post> = arrayListOf(
            Post(1,"board1","text1","user1","reg1",0),
            Post(2,"board2","text2","user2","reg2",0),
            Post(3,"board3","text3","user3","reg3",0),
            Post(4,"board4","text4","user4","reg4",0),
        )

        rv_scrapBoard.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_scrapBoard.setHasFixedSize(true)


        rv_scrapBoard.adapter = BoardAdapter(sampleScrapList,layoutInflater,itemClick = {post ->
            toast("${post.title},${post.body}")
        })


    }
}