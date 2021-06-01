package com.example.capstone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.ScrapActivity
import com.example.capstone.adapter.BoardAdapter
import com.example.capstone.adapter.BoardListAdapter
import com.example.capstone.dataclass.Board
import com.example.capstone.dataclass.Post
import kotlinx.android.synthetic.main.fragment_board_list.*
import org.jetbrains.anko.support.v4.toast

class BoardListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // toolbar 설정 (지우지 말아주세요ㅜㅜ)
//        val toolbar = (activity as MainActivity).supportActionBar
//        toolbar?.setDisplayHomeAsUpEnabled(false)
//        toolbar?.setDisplayShowTitleEnabled(false)
        return inflater.inflate(R.layout.fragment_board_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val boardSample = arrayListOf<Board>(
            Board(R.id.iv_boardIcon,"자유게시판"),
            Board(R.id.iv_boardIcon,"비밀게시판"),
            Board(R.id.iv_boardIcon,"졸업생게시판")
        )

        rv_boardList.adapter = BoardListAdapter(boardSample, LayoutInflater.from(this.activity))
        rv_boardList.layoutManager = LinearLayoutManager(this.activity)
        rv_boardList.setHasFixedSize(true)
    }
}