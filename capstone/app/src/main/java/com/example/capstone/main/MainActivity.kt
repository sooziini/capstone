package com.example.capstone.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.example.capstone.*
import com.example.capstone.board.BoardActivity
import com.example.capstone.board.ScrapActivity
import com.example.capstone.network.MasterApplication
import com.example.capstone.setting.SettingActivity
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var dayn: String
    lateinit var studentGradeId: String     // 학번
    lateinit var studentName: String        // 이름
    lateinit var studentYear: String        // 입학년도
    lateinit var studentId: String          // 아이디
    private var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.home_menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        val instance = Calendar.getInstance()
        val month = (instance.get(Calendar.MONTH) + 1).toString()
        val day = instance.get(Calendar.DATE).toString()

        when (instance.get(Calendar.DAY_OF_WEEK)) {
            1 -> dayn = "일"
            2 -> dayn = "월"
            3 -> dayn = "화"
            4 -> dayn = "수"
            5 -> dayn = "목"
            6 -> dayn = "금"
            7 -> dayn = "토"
        }

        Home_DateText.text = month + "월 " + day + "일 (" + dayn + ")"

        (application as MasterApplication).service.authorization()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        if(response.body()!!["success"].toString() == "true") {
                            val data = response.body()!!["data"] as LinkedTreeMap<String, Any>
                            studentYear = (data["year"] as Double).roundToInt().toString()
                            
                            val stug = (data["schoolgrade"] as Double).roundToInt().toString()
                            var stuc = (data["schoolclass"] as Double).roundToInt().toString()
                            var stun = (data["schoolnumber"] as Double).roundToInt().toString()
                            if (stuc.toInt() < 10)
                                stuc = "0$stuc"
                            if (stun.toInt() < 10)
                                stun = "0$stun"
                            studentGradeId = stug + stuc + stun
                            studentName = data["name"].toString()
                            studentId = data["id"].toString()
                            Home_WelcomeText.text = studentGradeId + " " + studentName + "님, 환영합니다!"
                        } else {
                            toast("데이터를 조회할 수 없습니다")
                            finish()
                        }
                    } else {
                        toast("데이터를 조회할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })

        supportFragmentManager.beginTransaction()
            .replace(R.id.Home_TimeTableFrameLayout, TimeTableFragment())
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.Home_TodoListFrameLayout, TodoListFragment())
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.Home_MealFrameLayout, SchoolMealFragment())
            .commit()

        main_menu_navigationview.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_main_menu_home -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_main_menu_school -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://sangmyung-agh.sen.hs.kr/index.do"))
                    startActivity(intent)
                    finish()
                    true
                }
                // 전교생 자유게시판
                R.id.drawer_board_menu_all -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(this, BoardActivity::class.java)
                    intent.putExtra("type", "all_free")
                    startActivity(intent)
                    finish()
                    true
                }
                // 학년별 자유게시판
                R.id.drawer_board_menu_grade -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(this, BoardActivity::class.java)
                    val type = when(studentYear) {
                        "2021" -> "1st_free"
                        "2020" -> "2nd_free"
                        "2019" -> "3rd_free"
                        else -> "error"
                    }
                    intent.putExtra("type", type)
                    startActivity(intent)
                    finish()
                    true
                }
                // 학생 건의함
                R.id.drawer_activity_menu_sug -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(this, BoardActivity::class.java)
                    intent.putExtra("type", "sug")
                    startActivity(intent)
                    finish()
                    true
                }
                // 학생회 공지
                R.id.drawer_activity_menu_notice -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(this, BoardActivity::class.java)
                    intent.putExtra("type", "notice")
                    startActivity(intent)
                    finish()
                    true
                }
                // 동아리 활동
                R.id.drawer_activity_menu_club -> {
                    main_drawerlayout.closeDrawers()
                    val intent = Intent(this, BoardActivity::class.java)
                    intent.putExtra("type", "club")
                    startActivity(intent)
                    finish()
                    true
                }
                // 선생님과의 대화
//                R.id.drawer_my_menu_message -> {
//                    main_drawerlayout.closeDrawers()
//                    startActivity(Intent(this, MessageActivity::class.java))
//                    finish()
//                    true
//                }
                // 게시글 보관함
                R.id.drawer_my_menu_scrap -> {
                    main_drawerlayout.closeDrawers()
                    startActivity(Intent(this, ScrapActivity::class.java))
                    finish()
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
                finish()
                return true
            }
            // 마이페이지
            R.id.main_menu_myinfo -> {
                val intent = Intent(this, SettingActivity::class.java)
                intent.putExtra("user_id", studentId)
                intent.putExtra("user_name", studentName)
                intent.putExtra("user_student_id", studentGradeId)
                startActivity(intent)
                finish()
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
            if(System.currentTimeMillis() - mBackWait >= 2000 ) {
                mBackWait = System.currentTimeMillis()
                toast("뒤로가기 버튼을 한번 더 누르면 종료됩니다")
            } else {
                finish()
            }
        }
    }
}