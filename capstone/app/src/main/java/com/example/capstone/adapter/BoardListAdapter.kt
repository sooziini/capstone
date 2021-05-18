package com.example.capstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.R.*
import com.example.capstone.dataclass.Board

class BoardListAdapter(
//                    val context: Context,
                    val BoardList : ArrayList<Board>,
                    val inflater: LayoutInflater
                    ):RecyclerView.Adapter<BoardListAdapter.BoardListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        val view = inflater.inflate(R.layout.board_item,parent,false)
        return BoardListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        return holder.bind(BoardList[position],position)
    }

    override fun getItemCount(): Int {
        return BoardList.size
    }

    inner class BoardListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val boardImg = itemView.findViewById<ImageView>(R.id.iv_boardIcon)
        val boardName = itemView.findViewById<TextView>(R.id.tv_boardName)

        fun bind (board: Board,position: Int){
            boardImg.setImageResource(board.board_img)
            boardName.text = board.board_name
        }

    }
}