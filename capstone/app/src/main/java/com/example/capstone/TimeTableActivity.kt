package com.example.capstone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_time_table.*

class TimeTableActivity : AppCompatActivity() {
    private lateinit var sp : SharedPreferences

    private var editMode = false

    private val monList = arrayOf("Mon1", "Mon2", "Mon3", "Mon4", "Mon5", "Mon6", "Mon7")
    private val tueList = arrayOf("Tue1", "Tue2", "Tue3", "Tue4", "Tue5", "Tue6", "Tue7")
    private val wedList = arrayOf("Wed1", "Wed2", "Wed3", "Wed4", "Wed5", "Wed6", "Wed7")
    private val thuList = arrayOf("Thu1", "Thu2", "Thu3", "Thu4", "Thu5", "Thu6", "Thu7")
    private val friList = arrayOf("Fri1", "Fri2", "Fri3", "Fri4", "Fri5", "Fri6", "Fri7")
    private val satList = arrayOf("Sat1", "Sat2", "Sat3", "Sat4", "Sat5", "Sat6", "Sat7")

    private var monday = ArrayList<EditText>()
    private var tuesday = ArrayList<EditText>()
    private var wednesday = ArrayList<EditText>()
    private var thursday = ArrayList<EditText>()
    private var friday = ArrayList<EditText>()
    private var saturday = ArrayList<EditText>()

    private var dayArray = ArrayList<ArrayList<EditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)

        // toolbar 설정
        setSupportActionBar(timetable_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        sp = getSharedPreferences("timetable", Context.MODE_PRIVATE)

        monday = arrayListOf(TimeTable_Mon1, TimeTable_Mon2, TimeTable_Mon3, TimeTable_Mon4, TimeTable_Mon5, TimeTable_Mon6, TimeTable_Mon7)
        tuesday = arrayListOf(TimeTable_Tue1, TimeTable_Tue2, TimeTable_Tue3, TimeTable_Tue4, TimeTable_Tue5, TimeTable_Tue6, TimeTable_Tue7)
        wednesday = arrayListOf(TimeTable_Wed1, TimeTable_Wed2, TimeTable_Wed3, TimeTable_Wed4, TimeTable_Wed5, TimeTable_Wed6, TimeTable_Wed7)
        thursday = arrayListOf(TimeTable_Thu1, TimeTable_Thu2, TimeTable_Thu3, TimeTable_Thu4, TimeTable_Thu5, TimeTable_Thu6, TimeTable_Thu7)
        friday = arrayListOf(TimeTable_Fri1, TimeTable_Fri2, TimeTable_Fri3, TimeTable_Fri4, TimeTable_Fri5, TimeTable_Fri6, TimeTable_Fri7)
        saturday = arrayListOf(TimeTable_Sat1, TimeTable_Sat2, TimeTable_Sat3, TimeTable_Sat4, TimeTable_Sat5, TimeTable_Sat6, TimeTable_Sat7)

        dayArray = arrayListOf(monday, tuesday, wednesday, thursday, friday, saturday)

        loadData(sp)

    }

    override fun onPause() {
        super.onPause()

        saveData(sp)
    }

    fun setEditMode() {
        for (day in dayArray) {
            for (textView in day)
                textView.isEnabled = true
        }
    }

    fun doneEditMode() {
        for (day in dayArray) {
            for (textView in day)
                textView.isEnabled = false
        }
    }

    fun saveData(sp: SharedPreferences) {
        val editor = sp.edit()

        for (i in 0..6) {
            editor.putString(monList[i], monday[i].text.toString())
        }
        for (i in 0..6) {
            editor.putString(tueList[i], tuesday[i].text.toString())
        }
        for (i in 0..6) {
            editor.putString(wedList[i], wednesday[i].text.toString())
        }
        for (i in 0..6) {
            editor.putString(thuList[i], thursday[i].text.toString())
        }
        for (i in 0..6) {
            editor.putString(friList[i], friday[i].text.toString())
        }
        for (i in 0..6) {
            editor.putString(satList[i], saturday[i].text.toString())
        }

        editor.apply()
    }


    fun loadData(sp: SharedPreferences) {
        for(i in 0..6) {
            monday[i].setText(sp.getString(monList[i], ""))
        }
        for(i in 0..6) {
            tuesday[i].setText(sp.getString(tueList[i], ""))
        }
        for(i in 0..6) {
            wednesday[i].setText(sp.getString(wedList[i], ""))
        }
        for(i in 0..6) {
            thursday[i].setText(sp.getString(thuList[i], ""))
        }
        for(i in 0..6) {
            friday[i].setText(sp.getString(friList[i], ""))
        }
        for(i in 0..6) {
            saturday[i].setText(sp.getString(satList[i], ""))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.timetable_menu, menu)
        return true
    }

    fun timetableOnClick(item: MenuItem) {
        if(!editMode) {
            setEditMode()
            item.setIcon(R.drawable.timetable_done)
            editMode = true
        } else {
            doneEditMode()
            item.setIcon(R.drawable.timetable_edit)
            editMode = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}