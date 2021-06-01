package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.capstone.fragment.*
import kotlinx.android.synthetic.main.activity_board_write.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)       // 기본 뒤로가기 버튼 false
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        replaceFragment(HomeFragment())
        main_toolbar.visibility = View.GONE

        // bottom navigation item이 선택되면
        // 해당되는 fragment로 전환
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    replaceFragment(HomeFragment())
                    main_toolbar.visibility = View.GONE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_timetable -> {
                    replaceFragment(TimeTableFragment())
                    main_toolbar_title.text = "시간표"
                    main_toolbar.visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_board_list -> {
                    replaceFragment(BoardListFragment())
                    main_toolbar_title.text = "게시판"
                    main_toolbar.visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_notice -> {
                    replaceFragment(NoticeFragment())
                    main_toolbar_title.text = "알림"
                    main_toolbar.visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_myinfo -> {
                    replaceFragment(MyInfoFragment())
                    main_toolbar_title.text = "내 정보"
                    main_toolbar.visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }

    }

    // fragment를 교체하는 함수
    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()    // 시작
        fragmentTransaction.replace(R.id.frameLayout_main_fragment, fragment)    // 할 일
        fragmentTransaction.commit()    // 끝
    }
}