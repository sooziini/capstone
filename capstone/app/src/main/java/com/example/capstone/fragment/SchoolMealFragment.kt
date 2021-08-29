package com.example.capstone.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.MealActAdapter
import com.example.capstone.dataclass.Meal
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_school_meal.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SchoolMealFragment : Fragment() {
    var mealList = ArrayList<Meal>()
    private val cal = Calendar.getInstance()

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

        MealFrag_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(requireContext()))
        MealFrag_RecyclerView.layoutManager = LinearLayoutManager(requireContext())
        MealFrag_RecyclerView.setHasFixedSize(true)
    }

    private fun getMealList() {
        val year = cal.get(Calendar.YEAR).toString()
        var month = (cal.get(Calendar.MONTH) + 1).toString()
        var day = cal.get(Calendar.DAY_OF_MONTH).toString()
        if (month.length < 2)
            month = "0${month}"
        if (day.length < 2)
            day = "0${day}"
        val date = year + month + day

        (activity?.application as MasterApplication).service.loadMeal(date, date)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        mealList.clear()

                        val dataArray = response.body()!!["mealInfo"] as ArrayList<LinkedTreeMap<String, String>>
                        Log.d("meal", dataArray.toString())
                        val todaymeal = dataArray[0]
                        val mealData = todaymeal["DDISH_NM"]
                        val mealArray = mealData?.split("<br/>")
                        Log.d("mealArray", mealArray.toString())

                        for (element in mealArray!!) {
                            mealList.add(Meal(element))
                        }

                        MealFrag_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(requireContext()))
                        MealFrag_RecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        MealFrag_RecyclerView.setHasFixedSize(true)
                    } else {
                        mealList.clear()
                        MealFrag_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(requireContext()))
                        MealFrag_RecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        MealFrag_RecyclerView.setHasFixedSize(true)
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
//                    toast("network error3")
                }
            })
    }
}