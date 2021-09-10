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
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SchoolMealActivity : AppCompatActivity() {
    private val cal = Calendar.getInstance()
    private val year = cal.get(Calendar.YEAR)
    private val month = cal.get(Calendar.MONTH)
    private val day = cal.get(Calendar.DAY_OF_MONTH)
    private lateinit var date: String
    var mealList = ArrayList<Meal>()

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
        School_Meal_DateText.text = "${tyear}년 ${tmonth}월 ${tday}일"
        retrofitLoadMeal(date)
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

    fun retrofitLoadMeal(date: String) {
        (application as MasterApplication).service.loadMeal(date, date)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()!!["success"].toString() == "true") {
                            mealList.clear()
                            val dataArray = response.body()!!["mealInfo"] as ArrayList<LinkedTreeMap<String, String>>
                            val todaymeal = dataArray[0]
                            val mealData = todaymeal["DDISH_NM"]
                            val mealArray = mealData?.split("<br/>")
                            val kcal = todaymeal["CAL_INFO"]

                            for (element in mealArray!!) mealList.add(Meal(element))
                            SchoolMeal_CalText.text = "열량 : $kcal"
                        } else {
                            mealList.clear()
                            SchoolMeal_CalText.text = ""
                        }
                        SchoolMeal_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(this@SchoolMealActivity))
                        SchoolMeal_RecyclerView.layoutManager = LinearLayoutManager(this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.setHasFixedSize(true)
                    } else {
                        mealList.clear()
                        SchoolMeal_RecyclerView.adapter = MealActAdapter(mealList, LayoutInflater.from(this@SchoolMealActivity))
                        SchoolMeal_RecyclerView.layoutManager = LinearLayoutManager(this@SchoolMealActivity)
                        SchoolMeal_RecyclerView.setHasFixedSize(true)
                        SchoolMeal_CalText.text = ""
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    // 메뉴 캘린더 버튼
    fun selectDate(item: MenuItem) {
        val listener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
            // i년 i2월 i3일
            val tempi2 = setDateSize((i2 + 1).toString())
            val tempi3 = setDateSize(i3.toString())
            School_Meal_DateText.text = "${i}년 ${tempi2}월 ${tempi3}일"
            date = "$year" + tempi2 + tempi3
            retrofitLoadMeal(date)
        }

        val picker = DatePickerDialog(this, listener, year, month, day)
        picker.show()
    }

    // 날짜 형식화
    private fun setDateSize(data: String): String {
        return if (data.length < 2) {
            val temp = "0${data}"
            temp
        } else
            data
    }

}