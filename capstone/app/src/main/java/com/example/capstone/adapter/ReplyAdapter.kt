package com.example.capstone.adapter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.board.BoardDetailActivity
import com.example.capstone.R
import com.example.capstone.dataclass.Reply
import com.example.capstone.dataclass.ReplyChange
import com.example.capstone.network.MasterApplication
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
        private var popUserCheck: Boolean = true

        fun bind(reply: Reply, position: Int) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLikeCnt.text = reply.goodCount.toString()

            if (reply.goodCheck == "Y") {
                replyLikeBtn.setImageResource(R.drawable.detail_like_selected)
                replyVer = true
            }
            if (reply.userCheck == "N") popUserCheck = false

            // 대댓글 버튼 눌렀을 경우
            replyCommentBtn.setOnClickListener {
                setReplyDialog(reply, true)
            }

            // 댓글 좋아요 버튼 눌렀을 경우
            replyLikeBtn.setOnClickListener {
                retrofitGoodReply(reply.reply_id.toString(), reply.goodCount, replyLikeCnt, replyLikeBtn, replyVer)
            }

            // report 버튼 클릭 시 팝업메뉴 설정
            replyReportBtn.setOnClickListener {
                val pop = PopupMenu(context, replyReportBtn)
                menuInflater.inflate(R.menu.board_reply_popup, pop.menu)

                if (!popUserCheck) pop.menu.findItem(R.id.board_reply_popup_delete).isVisible = false
                else pop.menu.findItem(R.id.board_reply_popup_report).isVisible = false

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        // 댓글 삭제하기 버튼
                        R.id.board_reply_popup_delete -> {
                            setDeleteReplyDialog(reply.board_id.toString(), reply.reply_id.toString(), position)
                        }
                        // 댓글 신고하기 버튼
                        R.id.board_reply_popup_report -> {
                            setReplyDialog(reply, false)
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
        private var popUserCheck: Boolean = true

        fun bind(reply: Reply, position: Int) {
            replyBody.text = reply.body
            replyDate.text = reply.regdate.substring(5, 16)
            replyLikeCnt.text = reply.goodCount.toString()

            if (reply.goodCheck == "Y") {
                replyLikeBtn.setImageResource(R.drawable.detail_like_selected)
                replyVer = true
            }
            if (reply.userCheck == "N") popUserCheck = false

            // 댓글 좋아요 버튼 눌렀을 경우
            replyLikeBtn.setOnClickListener {
                retrofitGoodReply(reply.reply_id.toString(), reply.goodCount, replyLikeCnt, replyLikeBtn, replyVer)
            }

            // report 버튼 클릭 시 팝업메뉴 설정
            replyReportBtn.setOnClickListener {
                val pop = PopupMenu(context, replyReportBtn)
                menuInflater.inflate(R.menu.board_reply_popup, pop.menu)

                if (!popUserCheck) pop.menu.findItem(R.id.board_reply_popup_delete).isVisible = false
                else pop.menu.findItem(R.id.board_reply_popup_report).isVisible = false

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        // 댓글 삭제하기 버튼
                        R.id.board_reply_popup_delete -> {
                            setDeleteReplyDialog(reply.board_id.toString(), reply.reply_id.toString(), position)
                        }
                        // 댓글 신고하기 버튼
                        R.id.board_reply_popup_report -> {
                            setReplyDialog(reply, false)
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
                        context.toast("댓글 좋아요 실패")
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    (context as Activity).finish()
                }
            })
    }

    fun addReplyItem(reply: Reply) {
        replyList.add(reply)
        notifyDataSetChanged()
    }

    fun removeReplyItem(position: Int) {
        replyList.removeAt(position)
        notifyDataSetChanged()
    }

    //댓글 삭제하기 버튼 클릭 시 뜨는 dialog 설정 함수
    fun setDeleteReplyDialog(board_id: String, reply_id: String, position: Int) {
        val builder = AlertDialog.Builder(context)
        val dialogView = inflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "해당 댓글을 삭제하시겠습니까?"

        builder.setPositiveButton("확인") { dialog, it ->
            (application as MasterApplication).service.deleteReply(board_id, reply_id)
                .enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse(
                        call: Call<HashMap<String, String>>,
                        response: Response<HashMap<String, String>>
                    ) {
                        if (response.isSuccessful && response.body()!!["success"] == "true") {
                            removeReplyItem(position)
                            (context as BoardDetailActivity).deleteReply()
                        } else {
                            context.toast("댓글 삭제 실패")
                        }
                    }

                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        (context as BoardDetailActivity).finish()
                    }
                })
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    // 대댓글 & 댓글 신고하기 버튼 클릭 시 뜨는 dialog 설정 함수
    fun setReplyDialog(reply: Reply, ver: Boolean) {
        val builder = AlertDialog.Builder(context)
        val dialogView = inflater.inflate(R.layout.dialog_reply, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.dialog_reply_edittext)
        dialogEditText.hint = if (ver) "대댓글을 입력해 주세요" else "신고 사유를 입력해 주세요"

        builder.setPositiveButton("확인") { dialog, it ->
            val body = dialogEditText.text.toString()
            if (ver) retrofitCreateReplyReply(reply.board_id.toString(), reply.reply_id.toString(), body)
            else retrofitReportReply(reply.reply_id.toString(), reply.user_id, body)
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    // 대댓글 작성 함수
    private fun retrofitCreateReplyReply(board_id: String, reply_id: String, body: String) {
        (application as MasterApplication).service.createReplyReply(board_id, reply_id, body)
            .enqueue(object : Callback<ReplyChange> {
                override fun onResponse(
                    call: Call<ReplyChange>,
                    response: Response<ReplyChange>
                ) {
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        // val reply = response.body()!!.data
                        // addReplyItem(reply)
                        (context as BoardDetailActivity).finish()
                        val intent = Intent(context, BoardDetailActivity::class.java)
                        intent.putExtra("board_id", board_id)
                        intent.putExtra("activity_num", "0")
                        context.startActivity(intent)
                    } else {
                        context.toast("대댓글 작성 실패")
                    }
                }

                override fun onFailure(call: Call<ReplyChange>, t: Throwable) {
                    (context as BoardDetailActivity).finish()
                }
            })
    }

    // 댓글 신고 함수
    private fun retrofitReportReply(reply_id: String, recv_id: String, body: String) {
        val params = HashMap<String, Any>()
        params["reply_id"] = reply_id
        params["recv_id"] = recv_id
        params["body"] = body
        (application as MasterApplication).service.reportReply(params)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"] == "true") {
                        context.toast("신고가 접수되었습니다")
                    } else {
                        context.toast("댓글 신고 실패")
                    }
                }

                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    (context as BoardDetailActivity).finish()
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
                (holder as ParentViewHolder).bind(replyList[position], position)
                holder.setIsRecyclable(false)
            }
            else -> {
                (holder as ChildViewHolder).bind(replyList[position], position)
                holder.setIsRecyclable(false)
            }
        }
    }
}