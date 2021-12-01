package com.sangcom.capstone.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.sangcom.capstone.R
import com.sangcom.capstone.adapter.TimeTableAdapter
import com.sangcom.capstone.dataclass.StuClass
import com.sangcom.capstone.network.MasterApplication
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
            2 -> "mon"
            3 -> "tue"
            4 -> "wed"
            5 -> "thu"
            6 -> "fri"
            else -> ""
        }

        loadDept(dayText)
    }

    private fun loadDept(dayText: String){
        val classList = ArrayList<StuClass>()
        var todayList: LinkedTreeMap<String, LinkedTreeMap<String, String>>?
        classList.clear()

        if (dayText == "") {
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
                        val data = response.body()!!["table"] as LinkedTreeMap<String, LinkedTreeMap<String, LinkedTreeMap<String, String>>>?
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
                        requireContext().toast("데이터를 조회할 수 없습니다")
                        (context as Activity).finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    requireContext().toast("network error")
                    (context as Activity).finish()
                }
            })
    }

    private fun insertVal(classList: ArrayList<StuClass>, todayList: LinkedTreeMap<String, LinkedTreeMap<String, String>>, dayText: String) {
        var i = 0
        while (i <= 6) {
            val temp = "t${i + 1}"
            val time = todayList[temp] ?: null

            if (time == null) {
                i += 1
                continue
            }

            when {
                i < 4 -> {
                    val stuClass = StuClass(
                        period = i + 1,
                        day = dayText,
                        subject = time["subject"],
                        location = time["location"],
                        teacher = time["teacher"]
                    )
                    classList.add(stuClass)
                }
                i == 4 -> {
                    val stuClass = StuClass(
                        period = null,
                        day = dayText,
                        subject = "점심시간",
                        location = time["location"],
                        teacher = time["teacher"]
                    )
                    classList.add(stuClass)
                    val stuClass2 = StuClass(
                        period = i + 1,
                        day = dayText,
                        subject = time["subject"],
                        location = time["location"],
                        teacher = time["teacher"]
                    )
                    classList.add(stuClass2)
                }
                else -> {
                    val stuClass = StuClass(
                        period = i + 1,
                        day = dayText,
                        subject = time["subject"],
                        location = time["location"],
                        teacher = time["teacher"]
                    )
                    classList.add(stuClass)
                }
            }
            i += 1
        }
    }
}