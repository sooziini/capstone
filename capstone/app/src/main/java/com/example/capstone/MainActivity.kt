package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainLunchTableButton.setOnClickListener {
            // 추후 구현
        }

        MainFreeBoardButton.setOnClickListener {
            startActivity<FreeBoardActivity>()
        }

    }
}