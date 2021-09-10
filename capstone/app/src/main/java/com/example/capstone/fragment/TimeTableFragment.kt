package com.example.capstone.fragment

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
import com.example.capstone.dataclass.StuClass
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_time_table.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimeTableFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTimeTable()
    }

    private fun getTimeTable(){
        val instance = Calendar.getInstance()

        val dayText = when(instance.get(Calendar.DAY_OF_WEEK)) {
            1 -> "sun"
            2 -> "mon"
            3 -> "tue"
            4 -> "wed"
            5 -> "thu"
            6 -> "fri"
            7 -> "sat"
            else -> ""
        }

        loadDept(dayText)
    }

    private fun loadDept(dayText: String){
        val classList = ArrayList<StuClass>()
        var todayList: LinkedTreeMap<String, String>?
        classList.clear()

        if (dayText == "sun" || dayText == "") {
            TimeTable_RecyclerView.adapter = TimeTableAdapter(classList, LayoutInflater.from(activity))
            val layoutmanager = LinearLayoutManager(activity)
            layoutmanager.orientation = HORIZONTAL
            layoutmanager.canScrollHorizontally()
            TimeTable_RecyclerView.layoutManager = layoutmanager
            TimeTable_RecyclerView.setHasFixedSize(true)
            return
        }

        (activity?.application as MasterApplication).service.readTimeTable()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["table"] as LinkedTreeMap<String, LinkedTreeMap<String, String>>?
                        if (data != null) {
                            todayList = data[dayText]
                            todayList?.let { insertVal(classList, it, dayText) }
                        }
                        TimeTable_RecyclerView.adapter = TimeTableAdapter(classList, LayoutInflater.from(activity))
                        val layoutmanager = LinearLayoutManager(activity)
                        layoutmanager.orientation = HORIZONTAL
                        layoutmanager.canScrollHorizontally()
                        TimeTable_RecyclerView.layoutManager = layoutmanager
                        TimeTable_RecyclerView.setHasFixedSize(true)
                    } else {        // 3xx, 4xx 를 받은 경우
                        requireContext().toast("데이터 로드 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    requireContext().toast("network error")
                }
            })
    }

    private fun insertVal(classList: ArrayList<StuClass>, todayList: LinkedTreeMap<String, String>, dayText: String) {
        var i = 0
        while (i <= 6) {
            val temp = "t${i + 1}"
            val subject = todayList[temp] ?: null

            if (subject == null) {
                i += 1
                continue
            }

            when {
                i < 4 -> {
                    val stuClass = StuClass(
                        period = i + 1,
                        day = dayText,
                        subject = subject
                    )
                    classList.add(stuClass)
                }
                i == 4 -> {
                    val stuClass = StuClass(
                        period = null,
                        day = dayText,
                        subject = "점심시간"
                    )
                    classList.add(stuClass)
                    val stuClass2 = StuClass(
                        period = i + 1,
                        day = dayText,
                        subject = subject
                    )
                    Log.d("subject", subject.toString())
                    classList.add(stuClass2)
                }
                else -> {
                    val stuClass = StuClass(
                        period = i + 1,
                        day = dayText,
                        subject = subject
                    )
                    classList.add(stuClass)
                }
            }
            i += 1
        }
    }
}