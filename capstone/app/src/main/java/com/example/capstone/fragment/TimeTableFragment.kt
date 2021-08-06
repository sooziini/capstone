package com.example.capstone.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.capstone.R
import com.example.capstone.adapter.TimeTableAdapter
import com.example.capstone.database.FeedEntry
import com.example.capstone.database.FeedReaderDBHelper
import com.example.capstone.dataclass.StuClass
import kotlinx.android.synthetic.main.fragment_time_table.*
import org.jetbrains.anko.support.v4.toast
import java.util.*
import kotlin.collections.ArrayList

class TimeTableFragment: Fragment() {
    lateinit var dbHelper: FeedReaderDBHelper
    var classList: ArrayList<StuClass>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dbHelper = FeedReaderDBHelper(requireContext())
        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        classList = getTimeTable()
        Log.d(TAG, "classList: " + classList.toString())

        if (classList != null) {
            val deptAdapter = TimeTableAdapter(classList!!, LayoutInflater.from(this.activity))
            TimeTable_RecyclerView?.adapter = deptAdapter
            val layoutmanager = LinearLayoutManager(this.activity)
            layoutmanager.orientation = HORIZONTAL
            layoutmanager.canScrollHorizontally()

            TimeTable_RecyclerView?.layoutManager = layoutmanager
            TimeTable_RecyclerView?.setHasFixedSize(true)
        }
    }

    private fun getTimeTable(): ArrayList<StuClass>? {
        val instance = Calendar.getInstance()
        val dayNum = instance.get(Calendar.DAY_OF_WEEK)
        val year = instance.get(Calendar.YEAR).toString()
        val month = instance.get(Calendar.MONTH).toString()
        val day = instance.get(Calendar.DATE).toString()
        toast(year + " " + month +  " " + day)
        return loadData(dayNum)
    }

    private fun loadData(dayNum: Int): ArrayList<StuClass>? {
        lateinit var likeText: String

        when(dayNum) {
            1 -> {
//                day = "일"
                likeText = "Sun%"
//                return null
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
        if (likeText == "Sun%") {
            return null
        } else {
            val classList = loadDept(likeText)
            return classList
        }
    }

    private fun loadDept(likeText: String): ArrayList<StuClass> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(FeedEntry.COLUMN_NAME_DAYTIME, FeedEntry.COLUMN_NAME_DEPT)
        val selection = "${FeedEntry.COLUMN_NAME_DAYTIME} LIKE ?"
        val selectionArgs = arrayOf(likeText)
        val sortOrder = "${FeedEntry.COLUMN_NAME_DAYTIME} ASC"
        val start = arrayOf("8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00")
        val end = arrayOf("8:50", "9:50", "10:50", "11:50", "12:50", "13:50", "14:50", "15:50")
        val classList = ArrayList<StuClass>()

        val cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs,null,null, sortOrder)
        with(cursor) {
            var i = 0
            while(moveToNext()) {
                if (i < 4) {
                    val stuClass = StuClass(
                        classNum = i + 1,
                        className = cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)),
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass)
                } else if (i == 4) {
                    val stuClass = StuClass(
                        classNum = null,
                        className = "점심시간",
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass)
                    i = i + 1
                    val stuClass2 = StuClass(
                        classNum = i,
                        className = cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)),
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass2)
                } else {
                    val stuClass = StuClass(
                        classNum = i,
                        className = cursor.getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DEPT)),
                        startTime = start[i],
                        endTime = end[i]
                    )
                    classList.add(stuClass)
                }
                i += 1
            }
        }
        return classList
    }
}