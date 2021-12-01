package com.sangcom.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sangcom.capstone.dataclass.Post
import com.sangcom.capstone.R

class BoardAdapter (
    private var postList: ArrayList<Post>,
    private val inflater: LayoutInflater,
    private val itemClick: (Post) -> Unit
): RecyclerView.Adapter<BoardAdapter.PostViewHolder>() {

    // 뷰홀더 설정
    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val postTitle: TextView = itemView.findViewById(R.id.post_item_title)
        private val postBody: TextView = itemView.findViewById(R.id.post_item_body)
        private val postDate: TextView = itemView.findViewById(R.id.post_item_date)
        private val postUser: TextView = itemView.findViewById(R.id.post_item_nickname)
        private val postComment: TextView = itemView.findViewById(R.id.post_item_comment_cnt)
        private val postLike: TextView = itemView.findViewById(R.id.post_item_like_cnt)
        private val postScrap: TextView = itemView.findViewById(R.id.post_item_scrap_cnt)

        fun bind(post: Post) {
            postTitle.text = post.title
            postBody.text = post.body
            postDate.text = post.regdate.substring(5, 16)
            postComment.text = post.replyCount.toString()
            postLike.text = post.goodCount.toString()
            postScrap.text = post.scrapCount.toString()
            if (post.type == "notice") postUser.text = post.user_id

            itemView.setOnClickListener { itemClick(post) }
        }
    }

    fun refreshPostItem(posts: ArrayList<Post>) {
        postList = posts
        notifyDataSetChanged()
    }

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = inflater.inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    // recyclerview item 개수 return
    override fun getItemCount(): Int {
        return postList.size
    }

    // 뷰홀더에 post 하나씩 바인딩
    // onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터 연결
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position])
    }
}