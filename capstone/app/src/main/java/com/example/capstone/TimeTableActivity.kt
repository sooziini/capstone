package com.example.capstone

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private val monList = arrayOf("Mon1", "Mon2", "Mon3", "Mon4", "Mon5", "Mon6", "Mon7")
    private val tueList = arrayOf("Tue1", "Tue2", "Tue3", "Tue4", "Tue5", "Tue6", "Tue7")
    private val wedList = arrayOf("Wed1", "Wed2", "Wed3", "Wed4", "Wed5", "Wed6", "Wed7")
    private val thuList = arrayOf("Thu1", "Thu2", "Thu3", "Thu4", "Thu5", "Thu6", "Thu7")
    private val friList = arrayOf("Fri1", "Fri2", "Fri3", "Fri4", "Fri5", "Fri6", "Fri7")
    private val satList = arrayOf("Sat1", "Sat2", "Sat3", "Sat4", "Sat5", "Sat6", "Sat7")

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

    override fun onDestroy() {
        super.onDestroy()
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
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 시간표 저장하는 함수
    private fun saveData() {
        val dayList = arrayOf(monList, tueList, wedList, thuList, friList, satList)
//
//        for (i in dayList.indices) {
//            saveDept(db, dayArray[i], dayList[i])
//        }
    }

    // 시간표 조회하는 함수
    private fun loadData() {
        val dayText = arrayOf("mon", "tue", "wed", "thu", "fri", "sat")

//        for (i in 0..5) {
//            loadDept(db, dayArray[i], likeText[i])
//        }

        (application as MasterApplication).service.readTimeTable()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["table"] as LinkedTreeMap<String, HashMap<String, String>>
                        for (i in 0..5) {
                            val todayList = data[dayText[i]] ?: return

                            val dayEditList = when(dayText[i]) {
                                "mon" -> monday
                                "tue" -> tuesday
                                "wed" -> wednesday
                                "thu" -> thursday
                                "fri" -> friday
                                "sat" -> saturday
                                else -> ArrayList()
                            }

                            for (i in 0..6)
                                dayEditList[i].setText(todayList["t${i + 1}"].toString())
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

    private fun saveDept(db: SQLiteDatabase, dayList: ArrayList<EditText>, dTextList: Array<String>) {
//        for (i in 0..6) {
//            val contentVal = ContentValues()
//            contentVal.put(FeedEntry.COLUMN_NAME_DEPT, dayList[i].text.toString())
//
//            val arg = arrayOf(dTextList[i])
//            db.update(FeedEntry.TABLE_NAME, contentVal, "${FeedEntry.COLUMN_NAME_DAYTIME} = ?", arg)
//        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////
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
}
