package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.example.capstone.fragment.SchoolMealFragment
import com.example.capstone.fragment.TimeTableFragment
import com.example.capstone.fragment.TodoListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.home_menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        supportFragmentManager.beginTransaction()
            .replace(R.id.Home_TimeTableFragment, TimeTableFragment())
            .commit()

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.Home_TodoListFragment, TodoListFragment())
//            .commit()
//
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.Home_MealFragment, SchoolMealFragment())
//            .commit()

        main_menu_navigationview.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_main_menu_home -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_main_menu_school -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                // 전교생 자유게시판
                R.id.drawer_board_menu_all -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(this, BoardActivity::class.java)
                    intent.putExtra("type", "all_free")
                    startActivity(intent)
                    true
                }
                // 학년별 자유게시판
                R.id.drawer_board_menu_grade -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                // 학생 건의함
                R.id.drawer_activity_menu_sug -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                // 학생회 공지
                R.id.drawer_activity_menu_notice -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                // 동아리 활동
                R.id.drawer_activity_menu_club -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                // 선생님과의 대화
                R.id.drawer_my_menu_message -> {
                    main_drawerlayout.closeDrawers()
                    startActivity(Intent(this, MessageActivity::class.java))
                    true
                }
                // 게시글 보관함
                R.id.drawer_my_menu_scrap -> {
                    main_drawerlayout.closeDrawers()
                    startActivity(Intent(this, ScrapActivity::class.java))
                    true
                }
                else -> false
            }
        }

        Home_TimeTableButton.setOnClickListener {
            val intent = Intent(this, TimeTableActivity::class.java)
            startActivity(intent)
            finish()
        }

        Home_TodoListButton.setOnClickListener {
            val intent = Intent(this, TodoListActivity::class.java)
            startActivity(intent)
            finish()
        }

        Home_MealButton.setOnClickListener {
            val intent = Intent(this, SchoolMealActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // toolbar의 왼쪽 상단 버튼 클릭 시
            android.R.id.home -> {
                main_drawerlayout.openDrawer(GravityCompat.START)
                return true
            }
            // 알림
            R.id.main_menu_notice -> {
                startActivity(Intent(this, NoticeActivity::class.java))
                return true
            }
            R.id.main_menu_myinfo -> {
                return true
            }
            // 신고 및 경고
            R.id.main_menu_myinfo_report -> {
                startActivity(Intent(this, ReportActivity::class.java))
                return true
            }
            // 설정
            R.id.main_menu_myinfo_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                return true
            }
            // 로그아웃
            R.id.main_menu_myinfo_logout -> {
                return true
            }
        }
        main_drawerlayout.closeDrawers()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (main_drawerlayout.isDrawerOpen(GravityCompat.START)) {
            main_drawerlayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    // fragment를 교체하는 함수
//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()    // 시작
//        fragmentTransaction.replace(R.id.frameLayout_main_fragment, fragment)    // 할 일
//        fragmentTransaction.commit()    // 끝
//    }
}

