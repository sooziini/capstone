package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.capstone.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(HomeFragment())

        // bottom navigation item이 선택되면
        // 해당되는 fragment로 전환
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    replaceFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_timetable -> {
                    replaceFragment(TimeTableFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_board_list -> {
                    replaceFragment(BoardListFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_notice -> {
                    replaceFragment(NoticeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_myinfo -> {
                    replaceFragment(MyInfoFragment())
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