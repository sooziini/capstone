package com.example.capstone.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Reply
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_board_detail.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReplyAdapter(
    private val replyList: ArrayList<Reply>,
    private val inflater: LayoutInflater,
    private val context: Context,
    private val menuInflater: MenuInflater,
    private val application: Application
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ParentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val replyBody: TextView = itemView.findViewById(R.id.parent_reply_item_body)
        private val replyDate: TextView = itemView.findViewById(R.id.parent_reply_item_date)
        private val replyLikeCnt: TextView = itemView.findViewById(R.id.parent_reply_item_like_cnt)
        private val replyCommentBtn: ImageView = itemView.findViewById(R.id.parent_reply_item_comment_btn)
        private val replyLikeBtn: ImageView = itemView.findViewById(R.id.parent_reply_item_like_btn)
        private val replyReportBtn: ImageView = itemView.findViewById(R.id.parent_reply_item_report_btn)
        private var replyVer: Boolean = false

        fun bind(reply: Reply) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLikeCnt.text = reply.goodCount.toString()

            if (reply.goodCheck == "Y") {
                replyLikeBtn.setImageResource(R.drawable.detail_like_selected)
                replyVer = true
            }

            // 댓글 좋아요 버튼 눌렀을 경우
            replyLikeBtn.setOnClickListener {
                retrofitGoodReply(reply.reply_id.toString(), reply.goodCount, replyLikeCnt, replyLikeBtn, replyVer)
            }

            // report 버튼 클릭 시 팝업메뉴 설정
            replyReportBtn.setOnClickListener {
                val pop = PopupMenu(context, replyReportBtn)
                menuInflater.inflate(R.menu.board_reply_popup, pop.menu)

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
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
        private val replyLikeCnt: TextView = itemView.findViewById(R.id.child_reply_item_like_cnt)
        private val replyLikeBtn: ImageView = itemView.findViewById(R.id.child_reply_item_like_btn)
        private val replyReportBtn: ImageView = itemView.findViewById(R.id.child_reply_item_report_btn)
        private var replyVer: Boolean = false

        fun bind(reply: Reply) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLikeCnt.text = reply.goodCount.toString()

            if (reply.goodCheck == "Y") {
                replyLikeBtn.setImageResource(R.drawable.detail_like_selected)
                replyVer = true
            }

            // 댓글 좋아요 버튼 눌렀을 경우
            replyLikeBtn.setOnClickListener {
                retrofitGoodReply(reply.reply_id.toString(), reply.goodCount, replyLikeCnt, replyLikeBtn, replyVer)
            }

            // report 버튼 클릭 시 팝업메뉴 설정
            replyReportBtn.setOnClickListener {
                val pop = PopupMenu(context, replyReportBtn)
                menuInflater.inflate(R.menu.board_reply_popup, pop.menu)

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
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

    // 댓글 좋아요하는 함수
    fun retrofitGoodReply(reply_id: String, likeCnt: Int, replyLikeCnt: TextView, replyLikeBtn: ImageView, replyVer: Boolean) {
        (application as MasterApplication).service.goodReply(reply_id)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        val stat = response.body()!!["stat"]
                        // 안 누름 -> 누름
                        if (stat == "INSERT") {
                            if (replyVer) replyLikeCnt.text = (likeCnt).toString()
                            else replyLikeCnt.text = (likeCnt+1).toString()
                            replyLikeBtn.setImageResource(R.drawable.detail_like_selected)
                        } else if (stat == "DELETE") {
                            // 누름 -> 안 누름
                            if (replyVer) replyLikeCnt.text = (likeCnt-1).toString()
                            else replyLikeCnt.text = likeCnt.toString()
                            replyLikeBtn.setImageResource(R.drawable.detail_like)
                        }
                    } else {
                        // toast("댓글 좋아요 실패")
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    // finish()
                }
            })
    }

    // 댓글 삭제하는 함수
    fun retrofitDeleteReply(board_id: String, reply_id: String) {
        (application as MasterApplication).service.deleteReply(board_id, reply_id)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        // 삭제 dialog


                    } else {
                        // toast("댓글 삭제 실패")
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    // finish()
                }
            })
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