package com.example.capstone

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.example.capstone.database.TimeTableDBHelper
import kotlinx.android.synthetic.main.activity_time_table.*
import org.jetbrains.anko.toast

class TimeTableActivity : AppCompatActivity() {
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "timetable"
        const val COLUMN_NAME_DAYTIME = "daytime"
        const val COLUMN_NAME_DEPT = "dept"
    }

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

//    override fun onDestroy() {
//        dbHelper.close()
//        super.onDestroy()
//    }

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
        val dbHelper = TimeTableDBHelper(this) // DB
        val db: SQLiteDatabase = dbHelper.writableDatabase

//        val values = ContentValues().apply {
//            for (i in 0..6) {
//                put(FeedEntry.COLUMN_NAME_DAYTIME, monList[i])
//                put(FeedEntry.COLUMN_NAME_DEPT, monday[i].text.toString())
//            }
//            for (i in 0..6) {
//                put(tueList[i], tuesday[i].text.toString())
//            }
//            for (i in 0..6) {
//                put(wedList[i], wednesday[i].text.toString())
//            }
//            for (i in 0..6) {
//                put(thuList[i], thursday[i].text.toString())
//            }
//            for (i in 0..6) {
//                put(friList[i], friday[i].text.toString())
//            }
//            for (i in 0..6) {
//                put(satList[i], saturday[i].text.toString())
//            }
//        }

//        val newRowId = db?.insert(FeedEntry.TABLE_NAME, null, values)
//
//        if (newRowId!!.equals(-1)) {
//            alert ("시간표가 저장되지 않았습니다."){
//                yesButton {  }
//            }
//        }
        for (i in 0..6) {
            val contentVal = ContentValues()
            contentVal.put(FeedEntry.COLUMN_NAME_DEPT, monday[i].text.toString())

            val where = "${FeedEntry.COLUMN_NAME_DAYTIME}=?"
            val args = arrayOf(monList[i])
            val suc = db.update(FeedEntry.TABLE_NAME, contentVal, where, args)
            toast(suc.toString())
        }
        db.close()
    }

    // 시간표 조회하는 함수
    private fun loadData() {
        val dbHelper = TimeTableDBHelper(this) // DB
        val db = dbHelper.readableDatabase

        val projection = arrayOf(FeedEntry.COLUMN_NAME_DAYTIME, FeedEntry.COLUMN_NAME_DEPT)
        val selection = "${FeedEntry.COLUMN_NAME_DAYTIME} = ?"
        var selectionArgs = arrayOf("Mon%")
        val sortOrder = "${FeedEntry.COLUMN_NAME_DAYTIME} ASC"

        var cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                monday[i].setText(cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)))
                i += 1
            }
        }

        selectionArgs = arrayOf("Tue%")
        cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                tuesday[i].setText(cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)))
                i += 1
            }
        }

        selectionArgs = arrayOf("Wed%")
        cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                wednesday[i].setText(cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)))
                i += 1
            }
        }

        selectionArgs = arrayOf("Thu%")
        cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                thursday[i].setText(cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)))
                i += 1
            }
        }

        selectionArgs = arrayOf("Fri%")
        cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                friday[i].setText(cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)))
                i += 1
            }
        }

        selectionArgs = arrayOf("Sat%")
        cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                saturday[i].setText(cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)))
                i += 1
            }
        }

        db.close()
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