package com.example.capstone.board

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.viewpager2.widget.ViewPager2
import com.example.capstone.R
import com.example.capstone.adapter.PostImagePagerAdapter
import kotlinx.android.synthetic.main.activity_post_image_pager.*

class PostImagePagerActivity : AppCompatActivity() {
    private var scaleFactor = 1.0f
    var mScaleGestureDetector: ScaleGestureDetector? = null
    lateinit var img: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_image_pager)

        if (intent.hasExtra("uriPaths")) {
            val uriPaths = intent.getSerializableExtra("uriPaths") as ArrayList<Uri>
            val position = intent.getIntExtra("position", 0)
            val adapter = PostImagePagerAdapter(uriPaths, LayoutInflater.from(this@PostImagePagerActivity))
            post_img_viewpager.adapter = adapter
            //post_img_viewpager.adapter = PostImagePagerAdapter(uriPaths, LayoutInflater.from(this@PostImagePagerActivity))
            post_img_viewpager.currentItem = position
            post_img_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d("abc", position.toString())
                }
            })

            img = post_img_viewpager
            mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        } else {
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector?.onTouchEvent(event)
        return true
    }

    // 제스쳐 이벤트를 처리하는 클래스
    inner class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector!!.scaleFactor
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 2.0f))   // 최소 0.5배, 최소 2배

            // 이미지에 적용
            img.scaleX = scaleFactor
            img.scaleY = scaleFactor
            return true
        }
    }
}