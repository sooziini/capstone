package com.example.capstone.board

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.capstone.R
import com.example.capstone.adapter.PostImagePagerAdapter
import kotlinx.android.synthetic.main.activity_post_image_pager.*

class PostImagePagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_image_pager)

        if (intent.hasExtra("uriPaths")) {
            val uriPaths = intent.getSerializableExtra("uriPaths") as ArrayList<Uri>
            val position = intent.getIntExtra("position", 0)
            post_img_viewpager.adapter = PostImagePagerAdapter(uriPaths, LayoutInflater.from(this@PostImagePagerActivity))
            post_img_viewpager.currentItem = position
        } else {
            finish()
        }
    }
}