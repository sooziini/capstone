package com.example.capstone.fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.SQLite.FeedReaderDbHelper
import com.example.capstone.TimeTableActivity
import com.example.capstone.adapter.TimeTableAdapter
import com.example.capstone.dataclass.StuClass
import kotlinx.android.synthetic.main.fragment_time_table.*
import java.util.*
import kotlin.collections.ArrayList

class TimeTableFragment : Fragment() {
//    val dbHelper = FeedReaderDbHelper(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        val db = dbHelper.readableDatabase

        val classList : ArrayList<StuClass> = ArrayList()

//        getTimeTable(classList)

        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    private fun getTimeTable(classList: ArrayList<StuClass>) {
        val adapter = TimeTableAdapter(classList, LayoutInflater.from(requireContext()))

        val instance = Calendar.getInstance()
        val day = instance.get(Calendar.DAY_OF_WEEK)
        TimeTable_RecyclerView.adapter = adapter
        TimeTable_RecyclerView.layoutManager = LinearLayoutManager(requireContext())
        TimeTable_RecyclerView.setHasFixedSize(true)
    }

//    private fun loadData() {
//        val db = dbHelper.readableDatabase
//        val likeText = arrayOf("Mon%", "Tue%", "Wed%", "Thu%", "Fri%", "Sat%")
//
//        for (i in 0..5) {
//            loadDept(db, dayArray[i], likeText[i])
//        }
//    }
//
//    private fun loadDept(db: SQLiteDatabase, dayList: ArrayList<EditText>, likeText: String) {
//        val projection = arrayOf(TimeTableActivity.FeedEntry.COLUMN_NAME_DAYTIME, TimeTableActivity.FeedEntry.COLUMN_NAME_DEPT)
//        val selection = "${TimeTableActivity.FeedEntry.COLUMN_NAME_DAYTIME} LIKE ?"
//        val selectionArgs = arrayOf(likeText)
//        val sortOrder = "${TimeTableActivity.FeedEntry.COLUMN_NAME_DAYTIME} ASC"
//
//        val cursor = db.query(TimeTableActivity.FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
//        with(cursor) {
//            var i = 0
//            while(moveToNext()) {
//                dayList[i].setText(cursor.getString(getColumnIndexOrThrow(TimeTableActivity.FeedEntry.COLUMN_NAME_DEPT)))
//                i += 1
//            }
//        }
//    }
}