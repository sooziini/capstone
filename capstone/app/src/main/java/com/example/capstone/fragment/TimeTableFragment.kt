package com.example.capstone.fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.TimeTableAdapter
import com.example.capstone.database.FeedEntry
import com.example.capstone.database.FeedReaderDBHelper
import com.example.capstone.dataclass.StuClass
import kotlinx.android.synthetic.main.fragment_time_table.*
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class TimeTableFragment : Fragment() {
    val dbHelper = FeedReaderDBHelper(requireContext())

    var classList = arrayListOf<StuClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val deptAdapter = TimeTableAdapter(requireContext(), classList)
        TimeTable_RecyclerView.adapter = deptAdapter

        getTimeTable(classList)
        val layoutManager = LinearLayoutManager(requireContext())
        TimeTable_RecyclerView.layoutManager = layoutManager
        TimeTable_RecyclerView.setHasFixedSize(true)


        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    private fun getTimeTable(classList: ArrayList<StuClass>) {
        val instance = Calendar.getInstance()
        val dayNum = instance.get(Calendar.DAY_OF_WEEK)
        lateinit var day: String

        loadData(dayNum, classList)
    }

    private fun loadData(dayNum: Int, classList: ArrayList<StuClass>) {
        val db = dbHelper.readableDatabase
        lateinit var likeText: String
        when(dayNum) {
            1 -> {
//                day = "일"
                likeText = "Sun"
                return
            }
            2 ->
                likeText = "Mon%"
            3 ->
                likeText = "Tue%"
            4 ->
                likeText = "Wed%"
            5 ->
                likeText = "Thu%"
            6 ->
                likeText = "Fri%"
            7 ->
                likeText = "Sat%"
        }

        loadDept(db, classList, likeText)
    }

    private fun loadDept(db: SQLiteDatabase, classList: ArrayList<StuClass>, likeText: String) {
        val projection = arrayOf(FeedEntry.COLUMN_NAME_DAYTIME, FeedEntry.COLUMN_NAME_DEPT)
        val selection = "${FeedEntry.COLUMN_NAME_DAYTIME} LIKE ?"
        val selectionArgs = arrayOf(likeText)
        val sortOrder = "${FeedEntry.COLUMN_NAME_DAYTIME} ASC"
        val start = arrayOf("8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00")
        val end = arrayOf("8:50", "9:50", "10:50", "11:50", "12:50", "13:50", "14:50", "15:50")

        val cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                if (i < 5) {
                    val stuClass = StuClass(
                        classNum = i,
                        className = cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)),
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass)
                } else if (i == 5) {
                    val stuClass = StuClass(
                        classNum = i,
                        className = "점심시간",
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass)
                } else {
                    val stuClass = StuClass(
                        classNum = i - 1,
                        className = cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)),
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass)
                }
                i += 1
            }
        }
    }
}