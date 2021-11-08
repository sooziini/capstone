package com.sangcom.capstone.master

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sangcom.capstone.R
import com.sangcom.capstone.adapter.StuListAdapter
import com.sangcom.capstone.dataclass.MasterStudent
import com.sangcom.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_student_list.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class StudentListActivity : AppCompatActivity() {
    private var leaderShow = false
    private var studentShow = false
    private val leaderArray = ArrayList<MasterStudent>()
    private val studentArray = ArrayList<MasterStudent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        // toolbar 설정
        setSupportActionBar(student_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        leader_arrow_button.setOnClickListener {
            if(!leaderShow) {
                leader_arrow_button.setImageResource(R.drawable.ic_arrow_up)
                leader_recyclerview.visibility = View.VISIBLE
            } else {
                leader_arrow_button.setImageResource(R.drawable.ic_arrow_down)
                leader_recyclerview.visibility = View.GONE
            }
            leaderShow = !leaderShow
        }

        student_arrow_button.setOnClickListener {
            if(!studentShow) {
                student_arrow_button.setImageResource(R.drawable.ic_arrow_up)
                student_recyclerview.visibility = View.VISIBLE
            } else {
                student_arrow_button.setImageResource(R.drawable.ic_arrow_down)
                student_recyclerview.visibility = View.GONE
            }
            studentShow = !studentShow
        }

        load()  // array 데이터 불러옴

        leader_recyclerview.adapter = StuListAdapter(leaderArray, LayoutInflater.from(this), this, application)
        leader_recyclerview.layoutManager = LinearLayoutManager(this)
        leader_recyclerview.setHasFixedSize(true)

        student_recyclerview.adapter = StuListAdapter(studentArray, LayoutInflater.from(this), this, application)
        student_recyclerview.layoutManager = LinearLayoutManager(this)
        student_recyclerview.setHasFixedSize(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity2::class.java))
        finish()
    }

    private fun load() {
        (application as MasterApplication).service.loadStudent()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        val list = response.body()!!["data"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (student in list) {
                            val id = student["id"] as String
                            val grade = (student["schoolgrade"] as Double).roundToInt().toString()
                            var cls = (student["schoolclass"] as Double).roundToInt().toString()
                            var num = (student["schoolnumber"] as Double).roundToInt().toString()
                            val name = student["name"].toString()
                            val role = student["role"].toString()

                            if (cls.length == 1)
                                cls = "0$cls"
                            if (num.length == 1)
                                num = "0$num"

                            val stuid = grade + cls + num

                            if (role == "leader") leaderArray.add(MasterStudent(id, stuid, name, "학생회"))
                            else if (role == "student") studentArray.add(MasterStudent(id, stuid, name, "학생"))
                        }
                    } else {
                        toast("로드 실패")
                    }
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }
}