package com.example.capstone.main

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.MealActivityAdapter
import com.example.capstone.adapter.MealDetailAdapter
import com.example.capstone.dataclass.Meal
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_school_meal.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SchoolMealActivity : AppCompatActivity() {
    private val cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH) + 1
    var itemList = ArrayList<ArrayList<Meal>>()
    val weekList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_meal)

        setSupportActionBar(school_meal_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        retrofitLoadMeal()

        val yearList = arrayOf("${year - 2}", "${year - 1}", "$year", "${year + 1}", "${year + 2}")
        meal_activity_year_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, yearList)
        meal_activity_year_spinner.setSelection(2, false)
        meal_activity_year_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                year = meal_activity_year_spinner.selectedItem.toString().toInt()
                retrofitLoadMeal()
            }
        }

        val monthList = Array(12) {i -> i + 1}
        meal_activity_month_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, monthList)
        meal_activity_month_spinner.setSelection(month - 1, false)
        meal_activity_month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                month = meal_activity_month_spinner.selectedItem.toString().toInt()
                retrofitLoadMeal()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 때
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun retrofitLoadMeal() {
        itemList.clear()
        weekList.clear()

        var textMonth = month.toString()
        cal.set(year, month, 1)
        val endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        if (textMonth.length < 2)
            textMonth = "0${month}"

        val startDate = year.toString() + textMonth + "01"
        val endDate = year.toString() + textMonth + endDay.toString()

        (application as MasterApplication).service.weekMeal(startDate, endDate)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()!!["success"].toString() == "true") {
                            val dataMap = response.body()!!["mealInfo"] as LinkedTreeMap<String, ArrayList<LinkedTreeMap<String, Any>>>

                            for (i in 1..5) {
                                val mealList = ArrayList<Meal>()
                                val array = dataMap["$i"]

                                if (array == null || array.isEmpty())
                                    continue

                                for (treeMap in array) {
                                    val receiveYear = treeMap["year"] as String
                                    val receiveMonth = treeMap["month"] as String
                                    val receiveDay = treeMap["day"] as String
                                    val mealDetailList = ArrayList<String>()
                                    val mealArray = treeMap["dish"] as ArrayList<String>
                                    for (item in mealArray) {
                                        val arr = item.split("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                                        mealDetailList.add(arr[0])
                                    }
                                    mealList.add(Meal(receiveYear, receiveMonth, receiveDay, mealDetailList))
                                }
                                itemList.add(mealList)
                                weekList.add(i)
                            }


                        } else {
                            itemList.clear()
                            weekList.clear()
                        }
                        SchoolMeal_RecyclerView.adapter = MealActivityAdapter(itemList, weekList, LayoutInflater.from(this@SchoolMealActivity), this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.layoutManager = LinearLayoutManager(this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.setHasFixedSize(true)
                    } else {
                        itemList.clear()
                        weekList.clear()
                        SchoolMeal_RecyclerView.adapter = MealActivityAdapter(itemList, weekList, LayoutInflater.from(this@SchoolMealActivity), this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.layoutManager = LinearLayoutManager(this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.setHasFixedSize(true)
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }
}