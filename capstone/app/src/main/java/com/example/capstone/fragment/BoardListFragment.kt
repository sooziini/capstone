package com.example.capstone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.ScrapActivity
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.adapter.BoardListAdapter
import com.example.capstone.dataclass.Board
import com.example.capstone.dataclass.Post
import kotlinx.android.synthetic.main.fragment_board_list.*

class BoardListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val boardSample = arrayListOf<Board>(
            Board(R.id.iv_boardIcon,"자유게시판"),
            Board(R.id.iv_boardIcon,"비밀게시판"),
            Board(R.id.iv_boardIcon,"졸업생게시판")
        )

        rv_boardList.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
        rv_boardList.setHasFixedSize(true)

        rv_boardList.adapter = BoardListAdapter(boardSample,this.layoutInflater)



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board_list, container, false)
    }

}