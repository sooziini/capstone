package com.example.capstone.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.TimeTableAdapter
import com.example.capstone.dataclass.StuClass
import kotlinx.android.synthetic.main.fragment_time_table.*

class TimeTableFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val classList : ArrayList<StuClass> = ArrayList()
//
//        val adapter = TimeTableAdapter(classList, LayoutInflater.from(activity))
//
//        TimeTable_RecyclerView.adapter = adapter
//        TimeTable_RecyclerView.layoutManager = LinearLayoutManager(activity)

        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }
}