package com.example.capstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.BoardDetailActivity
import com.example.capstone.R
import com.example.capstone.dataclass.Reply

class ReplyAdapter(
    private val replyList: ArrayList<Reply>,
    private val inflater: LayoutInflater,
    private val context: Context,
    private val menuInflater: MenuInflater
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ParentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val replyBody: TextView = itemView.findViewById(R.id.parent_reply_item_body)
        private val replyDate: TextView = itemView.findViewById(R.id.parent_reply_item_date)
        private val replyLike: TextView = itemView.findViewById(R.id.parent_reply_item_like_cnt)
        private val replyCommentBtn: ImageView = itemView.findViewById(R.id.parent_reply_item_comment_btn)
        private val replyLikeBtn: ImageView = itemView.findViewById(R.id.parent_reply_item_like_btn)
        private val replyReportBtn: ImageView = itemView.findViewById(R.id.parent_reply_item_report_btn)

        fun bind(reply: Reply) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLike.text = reply.goodCount.toString()

            // report 버튼 클릭 시 팝업메뉴 설정
            replyReportBtn.setOnClickListener {
                val pop = PopupMenu(context, replyReportBtn)
                menuInflater.inflate(R.menu.board_reply_popup, pop.menu)

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        // 댓글 수정하기 버튼
                        R.id.board_reply_popup_edit -> {

                        }
                        // 댓글 삭제하기 버튼
                        R.id.board_reply_popup_delete -> {

                        }
                        // 댓글 신고하기 버튼
                        R.id.board_reply_popup_report -> {

                        }
                    }
                    false
                }
                pop.show()
            }
        }
    }

    inner class ChildViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val replyBody: TextView = itemView.findViewById(R.id.child_reply_item_body)
        private val replyDate: TextView = itemView.findViewById(R.id.child_reply_item_date)
        private val replyLike: TextView = itemView.findViewById(R.id.child_reply_item_like_cnt)
        private val replyLikeBtn: ImageView = itemView.findViewById(R.id.child_reply_item_like_btn)
        private val replyReportBtn: ImageView = itemView.findViewById(R.id.child_reply_item_report_btn)

        fun bind(reply: Reply) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLike.text = reply.goodCount.toString()

            // report 버튼 클릭 시 팝업메뉴 설정
            replyReportBtn.setOnClickListener {
                val pop = PopupMenu(context, replyReportBtn)
                menuInflater.inflate(R.menu.board_reply_popup, pop.menu)

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        // 댓글 수정하기 버튼
                        R.id.board_reply_popup_edit -> {

                        }
                        // 댓글 삭제하기 버튼
                        R.id.board_reply_popup_delete -> {

                        }
                        // 댓글 신고하기 버튼
                        R.id.board_reply_popup_report -> {

                        }
                    }
                    false
                }
                pop.show()
            }
        }
    }

    fun setItems(reply: Reply) {
        replyList.clear()

        notifyDataSetChanged()
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