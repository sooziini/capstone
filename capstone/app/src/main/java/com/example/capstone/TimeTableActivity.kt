package com.example.capstone

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_time_table.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeTableActivity : AppCompatActivity() {
    private var editMode = false
    private val dayText = arrayOf("mon", "tue", "wed", "thu", "fri", "sat")

    private lateinit var monday: ArrayList<EditText>
    private lateinit var tuesday: ArrayList<EditText>
    private lateinit var wednesday: ArrayList<EditText>
    private lateinit var thursday: ArrayList<EditText>
    private lateinit var friday: ArrayList<EditText>
    private lateinit var saturday: ArrayList<EditText>

    private var dayArray = ArrayList<ArrayList<EditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)

        // toolbar 설정
        setSupportActionBar(timetable_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        monday = arrayListOf(TimeTable_Mon1, TimeTable_Mon2, TimeTable_Mon3, TimeTable_Mon4, TimeTable_Mon5, TimeTable_Mon6, TimeTable_Mon7)
        tuesday = arrayListOf(TimeTable_Tue1, TimeTable_Tue2, TimeTable_Tue3, TimeTable_Tue4, TimeTable_Tue5, TimeTable_Tue6, TimeTable_Tue7)
        wednesday = arrayListOf(TimeTable_Wed1, TimeTable_Wed2, TimeTable_Wed3, TimeTable_Wed4, TimeTable_Wed5, TimeTable_Wed6, TimeTable_Wed7)
        thursday = arrayListOf(TimeTable_Thu1, TimeTable_Thu2, TimeTable_Thu3, TimeTable_Thu4, TimeTable_Thu5, TimeTable_Thu6, TimeTable_Thu7)
        friday = arrayListOf(TimeTable_Fri1, TimeTable_Fri2, TimeTable_Fri3, TimeTable_Fri4, TimeTable_Fri5, TimeTable_Fri6, TimeTable_Fri7)
        saturday = arrayListOf(TimeTable_Sat1, TimeTable_Sat2, TimeTable_Sat3, TimeTable_Sat4, TimeTable_Sat5, TimeTable_Sat6, TimeTable_Sat7)

        dayArray = arrayListOf(monday, tuesday, wednesday, thursday, friday, saturday)

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    // 시간표 편집
    private fun setEditMode() {
        for (day in dayArray) {
            for (textView in day)
                textView.isEnabled = true
        }
    }

    // 시간표 편집 완료
    private fun doneEditMode() {
        for (day in dayArray) {
            for (textView in day)
                textView.isEnabled = false
        }
        saveData()
    }

    // 시간표 저장하는 함수
    private fun saveData() {
        val saveArray = ArrayList<HashMap<String, Any>>()
        val deleteArray = ArrayList<HashMap<String, Any>>()

        for (i in 0..5) {
            var j = 1
            for (textView in dayArray[i]) {
                val map = HashMap<String, Any>()
                if(textView.text.isNotEmpty()) {
                    map["subject"] = textView.text.toString()
                    map["days"] = dayText[i]
                    map["period"] = j
                    saveArray.add(map)
                } else if(textView.text.isEmpty() || textView.text.toString() == "") {
                    map["subject"] = "delete"
                    map["days"] = dayText[i]
                    map["period"] = j
                    deleteArray.add(map)
                }
                j += 1
            }
        }

        deleteData(deleteArray)

        val dataMap = HashMap<String, ArrayList<HashMap<String, Any>>>()
        dataMap["list"] = saveArray

        (application as MasterApplication).service.updateTimeTable(dataMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) { }
                    else {        // 3xx, 4xx 를 받은 경우
                        toast("데이터 저장 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                }
            })
    }

    // 시간표 조회하는 함수
    private fun loadData() {

        (application as MasterApplication).service.readTimeTable()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["table"] as LinkedTreeMap<String, LinkedTreeMap<String, String>>
                        for (i in 0..5) {
                            val todayList = data[dayText[i]] ?: continue

                            val dayEditList = when(dayText[i]) {
                                "mon" -> monday
                                "tue" -> tuesday
                                "wed" -> wednesday
                                "thu" -> thursday
                                "fri" -> friday
                                "sat" -> saturday
                                else -> ArrayList()
                            }

                            for (j in 0..6) {
                                if (todayList["t${j + 1}"] != null || todayList["t${j + 1}"] != "")
                                    dayEditList[j].setText(todayList["t${j + 1}"])
                            }
                        }
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("데이터 로드 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.timetable_menu, menu)
        return true
    }

    fun timetableOnClick(item: MenuItem) {
        editMode = if(!editMode) {
            setEditMode()
            item.setIcon(R.drawable.timetable_done)
            true
        } else {
            doneEditMode()
            item.setIcon(R.drawable.timetable_edit)
            false
        }
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun deleteData(array: ArrayList<HashMap<String, Any>>) {
        val deleteMap = HashMap<String, ArrayList<HashMap<String, Any>>>()

        deleteMap["list"] = array

        (application as MasterApplication).service.deleteTimeTable(deleteMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) { }
                    else {        // 3xx, 4xx 를 받은 경우
                        toast("데이터 로드 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                }
            })
    }
}
