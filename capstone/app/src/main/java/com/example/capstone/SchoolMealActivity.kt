package com.example.capstone

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.MealActAdapter
import com.example.capstone.dataclass.Meal
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_school_meal.*
import kotlinx.android.synthetic.main.activity_todo_list.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

class SchoolMealActivity : AppCompatActivity() {
    val cal = Calendar.getInstance()
    var year = cal.get(Calendar.YEAR)
    var month = cal.get(Calendar.MONTH)
    var day = cal.get(Calendar.DAY_OF_MONTH)
    lateinit var date: String
    val mealList = ArrayList<Meal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_meal)

        setSupportActionBar(school_meal_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        val tyear = year.toString()
        val tmonth = setDateSize((month + 1).toString())
        val tday = setDateSize(day.toString())

        date = tyear + tmonth + tday
        School_Meal_DateText.text = "${tyear}년 ${tmonth}월 ${tday}일 급식"

        (application as MasterApplication).service.loadMeal(date, date)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        mealList.clear()

                        val dataArray = response.body()!!.get("mealInfo") as ArrayList<LinkedTreeMap<String, String>>
                        Log.d("meal", dataArray.toString())
                        val todaymeal = dataArray[0]
                        val mealData = todaymeal.get("DDISH_NM")
                        val mealArray = mealData?.split("<br/>")
                        Log.d("mealArray", mealArray.toString())

                        for (element in mealArray!!) {
                            mealList.add(Meal(element))
                        }
                        SchoolMeal_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(this@SchoolMealActivity))
                        SchoolMeal_RecyclerView.layoutManager = LinearLayoutManager(this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.setHasFixedSize(true)
                    } else {
                        toast("load 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.school_meal_menu, menu)
        return super.onCreateOptionsMenu(menu)
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

    // 메뉴 캘린더 버튼
    fun selectDate(item: MenuItem) {
        var listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            // i년 i2월 i3일
            val tempi2 = setDateSize((i2 + 1).toString())
            val tempi3 = setDateSize(i3.toString())
            School_Meal_DateText.text = "${i}년 ${tempi2}월 ${tempi3}일"
            date = "${year}" + tempi2 + tempi3

            (application as MasterApplication).service.loadMeal(date, date)
                .enqueue(object : Callback<HashMap<String, Any>> {
                    override fun onResponse(
                        call: Call<HashMap<String, Any>>,
                        response: Response<HashMap<String, Any>>
                    ) {
                        if (response.isSuccessful) {
                            mealList.clear()

                            val dataArray = response.body()!!.get("mealInfo") as ArrayList<LinkedTreeMap<String, String>>
                            Log.d("meal", dataArray.toString())
                            val todaymeal = dataArray[0]
                            val mealData = todaymeal.get("DDISH_NM")
                            val mealArray = mealData?.split("<br/>")
                            Log.d("mealArray", mealArray.toString())

                            for (element in mealArray!!) {
                                mealList.add(Meal(element))
                            }
                            SchoolMeal_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(this@SchoolMealActivity))
                            SchoolMeal_RecyclerView.layoutManager = LinearLayoutManager(this@SchoolMealActivity)
                            SchoolMeal_RecyclerView.setHasFixedSize(true)
                        } else {
                            mealList.clear()

                            SchoolMeal_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(this@SchoolMealActivity))
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

        var picker = DatePickerDialog(this, listener, year, month, day)
        picker.show()
    }

    // 날짜 형식화
    private fun setDateSize(data: String): String {
        if (data.length < 2) {
            val temp = "0${data}"
            return temp
        }
        else
            return data
    }

}