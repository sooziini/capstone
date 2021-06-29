package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Reply

class ReplyAdapter(
    private val replyList: ArrayList<Reply>,
    private val inflater: LayoutInflater
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ParentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val replyBody: TextView = itemView.findViewById(R.id.parent_reply_item_body)
        private val replyDate: TextView = itemView.findViewById(R.id.parent_reply_item_date)
        private val replyLike: TextView = itemView.findViewById(R.id.parent_reply_item_like_cnt)

        fun bind(reply: Reply) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLike.text = reply.goodCount.toString()
        }
    }

    inner class ChildViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val replyBody: TextView = itemView.findViewById(R.id.child_reply_item_body)
        private val replyDate: TextView = itemView.findViewById(R.id.child_reply_item_date)
        private val replyLike: TextView = itemView.findViewById(R.id.child_reply_item_like_cnt)

        fun bind(reply: Reply) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLike.text = reply.goodCount.toString()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return replyList[position].level
    }

    override fun getItemCount(): Int {
        return replyList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            0 -> {
                view = inflater.inflate(R.layout.reply_parent_item, parent, false)
                ParentViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.reply_child_item, parent, false)
                ChildViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (replyList[position].level) {
            0 -> {
                (holder as ParentViewHolder).bind(replyList[position])
                holder.setIsRecyclable(false)
            }
            else -> {
                (holder as ChildViewHolder).bind(replyList[position])
                holder.setIsRecyclable(false)
            }
        }
    }
}