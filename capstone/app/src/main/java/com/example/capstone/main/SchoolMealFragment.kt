package com.example.capstone.main

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.adapter.MealDetailAdapter
import com.example.capstone.adapter.MealFragmentAdapter
import com.example.capstone.dataclass.Meal
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_school_meal.*
import kotlinx.android.synthetic.main.school_meal_item.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SchoolMealFragment : Fragment() {
    var mealList = ArrayList<Meal>()
    private val cal = Calendar.getInstance()
    lateinit var startDate: String
    lateinit var endDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_school_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMealList()
    }

    private fun getMealList() {
        val year = cal.get(Calendar.YEAR)
        var month = (cal.get(Calendar.MONTH) + 1)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        var textMonth = month.toString()
        var textDay = day.toString()

        if (textMonth.length < 2)
            textMonth = "0${month}"
        if (textDay.length < 2)
            textDay = "0${day}"

        val today = year.toString() + textMonth + textDay

        setDate(year, month, day)

        (activity?.application as MasterApplication).service.loadMeal(startDate, endDate)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        mealList.clear()
                        if (response.body()!!["success"].toString() == "true") {
                            val dataArray = response.body()!!["mealInfo"] as ArrayList<LinkedTreeMap<String, Any>>

                            for (todayMealList in dataArray) {
                                val mealDetailList = ArrayList<String>()
                                val mealArray = todayMealList["dish"] as ArrayList<String>
                                val year = todayMealList["year"] as String
                                val month = todayMealList["month"] as String
                                val day = todayMealList["day"] as String

                                for (mealData in mealArray) {
                                    val meal = mealData.split("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")
                                    mealDetailList.add(meal[0])
                                }
                                mealList.add(Meal(year, month, day, mealDetailList))
                            }
                        }
                        meal_fragment_rv1.adapter = MealFragmentAdapter(mealList, today, LayoutInflater.from(requireContext()), requireContext())
                        meal_fragment_rv1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        meal_fragment_rv1.setHasFixedSize(true)
                    } else {
                        mealList.clear()
                        meal_fragment_rv1.adapter = MealFragmentAdapter(mealList, today, LayoutInflater.from(requireContext()), requireContext())
                        meal_fragment_rv1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        meal_fragment_rv1.setHasFixedSize(true)
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    (context as Activity).finish()
                }
            })
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal[year, month - 1] = day

        // 일주일의 첫날을 일요일로 지정한다
        cal.firstDayOfWeek = Calendar.SUNDAY

        // 시작일과 특정날짜의 차이를 구한다
        val dayOfWeek = cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek

        // 해당 주차의 첫째날을 지정한다
        cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek)

        val sf = SimpleDateFormat("yyyyMMdd")

        // 해당 주차의 첫째 날짜
         startDate = sf.format(cal.time)

        // 해당 주차의 마지막 날짜 지정
         cal.add(Calendar.DAY_OF_MONTH, 6)

        // 해당 주차의 마지막 날짜
         endDate = sf.format(cal.time)
    }
}