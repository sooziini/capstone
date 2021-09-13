package com.example.capstone.board

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.PostImageAdapter
import com.example.capstone.adapter.ReplyAdapter
import com.example.capstone.dataclass.PostDetail
import com.example.capstone.dataclass.Reply
import com.example.capstone.dataclass.ReplyChange
import com.example.capstone.dataclass.ReplyListList
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_board_detail.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class BoardDetailActivity : AppCompatActivity() {

    var imm: InputMethodManager? = null
    private var menuUserCheck = true
    private lateinit var BASE_URL: String
    private lateinit var intentBoardId: String
    private lateinit var intentActivityNum: String
    private lateinit var boardDetailTitle: String
    private lateinit var boardDetailBody: String
    private lateinit var boardDetailType: String
    private lateinit var boardDetailUserId: String
    lateinit var boardDetailCommentCnt: String
    private lateinit var boardDetailGoodCnt: String
    private lateinit var boardDetailScrapCnt: String
    private lateinit var replyAdapter: ReplyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)

        BASE_URL = (application as MasterApplication).BASE_URL

        // toolbar 설정
        setSupportActionBar(board_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    }

    override fun onResume() {
        super.onResume()
        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("board_id")) {
            intentBoardId = intent.getStringExtra("board_id")!!
            intentActivityNum = intent.getStringExtra("activity_num")!!

            // 받은 board_id로 게시글 detail GET
            retrofitGetPostDetail(intentBoardId)

            // 받은 board_id로 댓글 GET
            retrofitGetReplyList(intentBoardId)
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        // 좋아요 버튼 눌렀을 경우
        board_detail_like_btn.setOnClickListener {
            retrofitGoodPostClick()
        }

        // 스크랩 버튼 눌렀을 경우
        board_detail_scrap_btn.setOnClickListener {
            retrofitScrapPostClick()
        }

        // 댓글 등록 버튼을 클릭했을 경우
        board_detail_comment_btn.setOnClickListener {
            val body = board_detail_comment.text.toString()

            if (body == "") {
                toast("댓글을 입력해 주세요")
            } else {
                // 댓글 작성 POST
                retrofitCreateReply(intentBoardId, body)
            }
        }

        // swipe refresh
        board_detail_swipeRefresh.setOnRefreshListener {
            retrofitGetPostDetail(intentBoardId)
            retrofitGetReplyList(intentBoardId)
            board_detail_swipeRefresh.isRefreshing = false
        }
    }

    // 받은 board_id로 게시글 detail GET하는 함수
    private fun retrofitGetPostDetail(board_id: String) {
        (application as MasterApplication).service.getPostDetail(board_id)
            .enqueue(object : Callback<PostDetail> {
                override fun onResponse(call: Call<PostDetail>, response: Response<PostDetail>) {
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val post = response.body()!!.data[0]
                        val postImgList = response.body()!!.imagepath
                        boardDetailTitle = post.title
                        boardDetailBody = post.body
                        boardDetailType = post.type
                        boardDetailUserId = post.user_id
                        boardDetailCommentCnt = post.replyCount.toString()
                        boardDetailGoodCnt = post.goodCount.toString()
                        boardDetailScrapCnt = post.scrapCount.toString()
                        board_detail_title.setText(boardDetailTitle).toString()
                        board_detail_body.setText(boardDetailBody).toString()
                        board_detail_date.setText(post.regdate.substring(0, 16)).toString()
                        board_detail_comment_cnt.setText(boardDetailCommentCnt).toString()
                        board_detail_like_cnt.setText(boardDetailGoodCnt).toString()
                        board_detail_scrap_cnt.setText(boardDetailScrapCnt).toString()
                        if (boardDetailType == "notice") board_detail_nickname.setText(boardDetailUserId).toString()

                        if (post.goodCheck == "Y")
                            board_detail_like_btn.setImageResource(R.drawable.detail_like_selected)
                        if (post.scrapCheck == "Y")
                            board_detail_scrap_btn.setImageResource(R.drawable.detail_scrap_selected)
                        if (post.userCheck == "N") menuUserCheck = false

                        // 사진이 있을 경우
                        if (postImgList.size > 0) {
                            val uriPaths: ArrayList<Uri> = ArrayList()
                            for (i in 0 until postImgList.size)
                                uriPaths.add(Uri.parse(BASE_URL+postImgList[i]))

                            val adapter = PostImageAdapter(uriPaths, LayoutInflater.from(this@BoardDetailActivity))
                            board_detail_img_recyclerview.adapter = adapter
                            board_detail_img_recyclerview.layoutManager = LinearLayoutManager(this@BoardDetailActivity).also {
                                it.orientation = LinearLayoutManager.HORIZONTAL
                            }
                        }
                    } else {
                        toast("게시글을 조회할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<PostDetail>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 받은 board_id로 댓글 GET하는 함수
    private fun retrofitGetReplyList(board_id: String) {
        (application as MasterApplication).service.getReplyList(board_id)
            .enqueue(object : Callback<ReplyListList> {
                override fun onResponse(
                    call: Call<ReplyListList>,
                    response: Response<ReplyListList>
                ) {
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val replyList = response.body()?.data
                        val reply = ArrayList<Reply>()

                        if (replyList != null && replyList.size > 0) {
                            // 새로운 replyList 생성
                            reply.clear()
                            for (i in 0 until replyList.size) {
                                reply.add(replyList[i].parent)
                                for (j in 0 until replyList[i].child.size)
                                    reply.add(replyList[i].child[j])
                            }
                        }
                        replyAdapter = ReplyAdapter(reply, LayoutInflater.from(this@BoardDetailActivity), this@BoardDetailActivity, menuInflater, application)
                        reply_recyclerview.adapter = replyAdapter
                        reply_recyclerview.layoutManager = LinearLayoutManager(this@BoardDetailActivity)
                        reply_recyclerview.setHasFixedSize(true)
                    } else {
                        toast("댓글을 조회할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<ReplyListList>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 입력받은 댓글 POST하는 함수
    private fun retrofitCreateReply(board_id: String, body: String) {
        (application as MasterApplication).service.createReply(board_id, body)
            .enqueue(object : Callback<ReplyChange> {
                override fun onResponse(
                    call: Call<ReplyChange>,
                    response: Response<ReplyChange>
                ) {
                    if (response.isSuccessful && response.body()!!.success == "true") {
                        val reply = response.body()!!.data
                        replyAdapter.addReplyItem(reply)
                        boardDetailCommentCnt = (boardDetailCommentCnt.toInt()+1).toString()
                        board_detail_comment_cnt.setText(boardDetailCommentCnt).toString()
                        board_detail_comment.setText("").toString()
                        hideKeyboard(board_detail_hidekeyboard)
                    } else {
                        toast("댓글을 작성할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<ReplyChange>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    fun deleteReply() {
        boardDetailCommentCnt = (boardDetailCommentCnt.toInt()-1).toString()
        board_detail_comment_cnt.setText(boardDetailCommentCnt).toString()
    }

    // 게시글 좋아요하는 함수
    private fun retrofitGoodPostClick() {
        (application as MasterApplication).service.goodPost(intentBoardId)
            .enqueue(object : Callback<HashMap<String, String>>{
            override fun onResponse(
                call: Call<HashMap<String, String>>,
                response: Response<HashMap<String, String>>
            ) {
                if (response.isSuccessful && response.body()!!["success"] == "true") {
                    val stat = response.body()!!["stat"]
                    // 안 누름 -> 누름
                    if (stat == "INSERT") {
                        boardDetailGoodCnt = (boardDetailGoodCnt.toInt()+1).toString()
                        board_detail_like_cnt.setText(boardDetailGoodCnt).toString()
                        board_detail_like_btn.setImageResource(R.drawable.detail_like_selected)
                    } else if (stat == "DELETE") {
                        // 누름 -> 안 누름
                        boardDetailGoodCnt = (boardDetailGoodCnt.toInt()-1).toString()
                        board_detail_like_cnt.setText(boardDetailGoodCnt).toString()
                        board_detail_like_btn.setImageResource(R.drawable.detail_like)
                    }
                } else {
                    toast("게시글 좋아요를 할 수 없습니다")
                    finish()
                }
            }

            override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                toast("network error")
                finish()
            }
        })
    }

    // 게시글 스크랩하는 함수
    private fun retrofitScrapPostClick() {
        (application as MasterApplication).service.scrapPost(intentBoardId)
            .enqueue(object : Callback<HashMap<String, String>> {
            override fun onResponse(
                call: Call<HashMap<String, String>>,
                response: Response<HashMap<String, String>>
            ) {
                if (response.isSuccessful && response.body()!!["success"] == "true") {
                    val stat = response.body()!!["stat"]
                    // 안 누름 -> 누름
                    if (stat == "INSERT") {
                        boardDetailScrapCnt = (boardDetailScrapCnt.toInt()+1).toString()
                        board_detail_scrap_cnt.setText(boardDetailScrapCnt).toString()
                        board_detail_scrap_btn.setImageResource(R.drawable.detail_scrap_selected)
                    } else if (stat == "DELETE") {
                        // 누름 -> 안 누름
                        boardDetailScrapCnt = (boardDetailScrapCnt.toInt()-1).toString()
                        board_detail_scrap_cnt.setText(boardDetailScrapCnt).toString()
                        board_detail_scrap_btn.setImageResource(R.drawable.detail_scrap)
                    }
                } else {
                    toast("게시글 스크랩을 할 수 없습니다")
                    finish()
                }
            }

            override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                toast("network error")
                finish()
            }
        })
    }

    // menu xml에서 설정한 menu를 붙임
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_detail_menu, menu)
        if (!menuUserCheck) menu?.setGroupVisible(R.id.board_detail_true, false)
        else menu?.findItem(R.id.board_detail_report)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.board_detail_edit -> {
                val intent = Intent(this, BoardWriteActivity::class.java)
                intent.putExtra("type", boardDetailType)
                intent.putExtra("board_write_id", intentBoardId)     // 글 수정의 경우 board_id 전달
                intent.putExtra("board_write_title", boardDetailTitle)
                intent.putExtra("board_write_body", boardDetailBody)
                startActivity(intent)
                finish()
                return true
            }
            // 삭제하기 버튼 클릭시 dialog 뜸
            R.id.board_detail_delete -> {
                // 현재 activity가 종료되었을 경우 dialog를 설정하지 않음
                if (!this.isFinishing)
                    setDeleteDialog()
                return true
            }
            R.id.board_detail_report -> {
                if (!this.isFinishing)
                    setReportDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (intentActivityNum) {
            "0" -> {
                val intent = Intent(this, BoardActivity::class.java)
                intent.putExtra("type", boardDetailType)
                startActivity(intent)
            }
            "1" -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("type", boardDetailType)
                startActivity(intent)
            }
            "2" -> {
                startActivity(Intent(this, ScrapActivity::class.java))
            }
        }
        finish()
    }

    // 게시글 삭제하기 버튼 눌렀을 때 뜨는 dialog 설정 함수
    private fun setDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialog_board_text)
        dialogText.text = "게시글을 삭제하시겠습니까?"

        builder.setPositiveButton("확인") { dialog, it ->
            (application as MasterApplication).service.deletePostDetail(intentBoardId)
                .enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse(
                        call: Call<HashMap<String, String>>,
                        response: Response<HashMap<String, String>>
                    ) {
                        if (response.isSuccessful && response.body()!!["success"] == "true") {
                            val intent = Intent(this@BoardDetailActivity, BoardActivity::class.java)
                            intent.putExtra("type", boardDetailType)
                            startActivity(intent)
                            finish()
                        } else {
                            toast("게시글을 삭제할 수 없습니다")
                            finish()
                        }
                    }

                    // 응답 실패 시
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        toast("network error")
                        finish()
                    }
                })
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    // 게시글 신고하기 버튼 눌렀을 때 뜨는 dialog 설정 함수
    private fun setReportDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_reply, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.dialog_reply_edittext)
        dialogEditText.hint = "신고 사유를 입력해 주세요"

        builder.setPositiveButton("확인") { dialog, it ->
            val body = dialogEditText.text.toString()
            val params = HashMap<String, Any>()
            params["board_id"] = intentBoardId
            params["recv_id"] = boardDetailUserId
            params["body"] = body
            (application as MasterApplication).service.reportPost(params)
                .enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse(
                        call: Call<HashMap<String, String>>,
                        response: Response<HashMap<String, String>>
                    ) {
                        if (response.isSuccessful && response.body()!!["success"] == "true") {
                            toast("신고가 접수되었습니다")
                        } else {
                            toast("게시글을 신고할 수 없습니다")
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        toast("network error")
                        finish()
                    }
                })
        }
            .setNegativeButton("취소", null)
        builder.setView(dialogView)
        builder.show()
    }

    fun hideKeyboard(v: View) {
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}