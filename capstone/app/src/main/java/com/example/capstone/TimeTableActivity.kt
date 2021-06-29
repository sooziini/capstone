package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_time_table.*


class TimeTableActivity : AppCompatActivity() {
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)

        TimeTable_EditButton.setOnClickListener {
            if(editMode == false) {
                setEditMode()
                TimeTable_EditButton.setImageResource(R.drawable.ic_baseline_done_24)
                editMode = true
            } else {
                doneEditMode()
                TimeTable_EditButton.setImageResource(R.drawable.ic_baseline_edit_24)
                editMode = false
            }
        }
    }

    fun setEditMode() {
        TimeTable_Mon1.isEnabled = true
        TimeTable_Mon2.isEnabled = true
        TimeTable_Mon3.isEnabled = true
        TimeTable_Mon4.isEnabled = true
        TimeTable_Mon5.isEnabled = true
        TimeTable_Mon6.isEnabled = true
        TimeTable_Mon7.isEnabled = true
        TimeTable_Tue1.isEnabled = true
        TimeTable_Tue2.isEnabled = true
        TimeTable_Tue3.isEnabled = true
        TimeTable_Tue4.isEnabled = true
        TimeTable_Tue5.isEnabled = true
        TimeTable_Tue6.isEnabled = true
        TimeTable_Tue7.isEnabled = true
        TimeTable_Wed1.isEnabled = true
        TimeTable_Wed2.isEnabled = true
        TimeTable_Wed3.isEnabled = true
        TimeTable_Wed4.isEnabled = true
        TimeTable_Wed5.isEnabled = true
        TimeTable_Wed6.isEnabled = true
        TimeTable_Wed7.isEnabled = true
        TimeTable_Thu1.isEnabled = true
        TimeTable_Thu2.isEnabled = true
        TimeTable_Thu3.isEnabled = true
        TimeTable_Thu4.isEnabled = true
        TimeTable_Thu5.isEnabled = true
        TimeTable_Thu6.isEnabled = true
        TimeTable_Thu7.isEnabled = true
        TimeTable_Fri1.isEnabled = true
        TimeTable_Fri2.isEnabled = true
        TimeTable_Fri3.isEnabled = true
        TimeTable_Fri4.isEnabled = true
        TimeTable_Fri5.isEnabled = true
        TimeTable_Fri6.isEnabled = true
        TimeTable_Fri7.isEnabled = true
        TimeTable_Sat1.isEnabled = true
        TimeTable_Sat2.isEnabled = true
        TimeTable_Sat3.isEnabled = true
        TimeTable_Sat4.isEnabled = true
        TimeTable_Sat5.isEnabled = true
        TimeTable_Sat6.isEnabled = true
        TimeTable_Sat7.isEnabled = true
    }

    fun doneEditMode() {
        TimeTable_Mon1.isEnabled = false
        TimeTable_Mon2.isEnabled = false
        TimeTable_Mon3.isEnabled = false
        TimeTable_Mon4.isEnabled = false
        TimeTable_Mon5.isEnabled = false
        TimeTable_Mon6.isEnabled = false
        TimeTable_Mon7.isEnabled = false
        TimeTable_Tue1.isEnabled = false
        TimeTable_Tue2.isEnabled = false
        TimeTable_Tue3.isEnabled = false
        TimeTable_Tue4.isEnabled = false
        TimeTable_Tue5.isEnabled = false
        TimeTable_Tue6.isEnabled = false
        TimeTable_Tue7.isEnabled = false
        TimeTable_Wed1.isEnabled = false
        TimeTable_Wed2.isEnabled = false
        TimeTable_Wed3.isEnabled = false
        TimeTable_Wed5.isEnabled = false
        TimeTable_Wed4.isEnabled = false
        TimeTable_Wed6.isEnabled = false
        TimeTable_Wed7.isEnabled = false
        TimeTable_Thu1.isEnabled = false
        TimeTable_Thu2.isEnabled = false
        TimeTable_Thu3.isEnabled = false
        TimeTable_Thu4.isEnabled = false
        TimeTable_Thu5.isEnabled = false
        TimeTable_Thu6.isEnabled = false
        TimeTable_Thu7.isEnabled = false
        TimeTable_Fri1.isEnabled = false
        TimeTable_Fri2.isEnabled = false
        TimeTable_Fri3.isEnabled = false
        TimeTable_Fri4.isEnabled = false
        TimeTable_Fri5.isEnabled = false
        TimeTable_Fri6.isEnabled = false
        TimeTable_Fri7.isEnabled = false
        TimeTable_Sat1.isEnabled = false
        TimeTable_Sat2.isEnabled = false
        TimeTable_Sat3.isEnabled = false
        TimeTable_Sat4.isEnabled = false
        TimeTable_Sat5.isEnabled = false
        TimeTable_Sat6.isEnabled = false
        TimeTable_Sat7.isEnabled = false
    }
}