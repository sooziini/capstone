package com.example.capstone.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R

class PostImageAdapter(
    private val uriPaths: ArrayList<Uri>,
    private val inflater: LayoutInflater,
    private val itemClick: (Int) -> Unit
): RecyclerView.Adapter<PostImageAdapter.PostImageViewHolder>() {
    inner class PostImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.post_img_itemView)

        fun bind(postUri: Uri, position: Int) {
            Glide.with(itemView.context)
                .load(postUri)
                .override(300)
                .placeholder(R.color.reply_child)
                .into(postImage)

            itemView.setOnClickListener { itemClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        val view = inflater.inflate(R.layout.post_img_item, parent, false)
        return PostImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bind(uriPaths[position], position)
    }

    override fun getItemCount(): Int = uriPaths.size
}