package com.example.capstone.main

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.capstone.R
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_time_table.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeTableActivity : AppCompatActivity() {
    private val dayText = arrayOf("mon", "tue", "wed", "thu", "fri")

    private lateinit var mondaydept: ArrayList<TextView>
    private lateinit var tuesdaydept: ArrayList<TextView>
    private lateinit var wednesdaydept: ArrayList<TextView>
    private lateinit var thursdaydept: ArrayList<TextView>
    private lateinit var fridaydept: ArrayList<TextView>

    private lateinit var mondayplace: ArrayList<TextView>
    private lateinit var tuesdayplace: ArrayList<TextView>
    private lateinit var wednesdayplace: ArrayList<TextView>
    private lateinit var thursdayplace: ArrayList<TextView>
    private lateinit var fridayplace: ArrayList<TextView>

    private lateinit var mondayteacher: ArrayList<TextView>
    private lateinit var tuesdayteacher: ArrayList<TextView>
    private lateinit var wednesdayteacher: ArrayList<TextView>
    private lateinit var thursdayteacher: ArrayList<TextView>
    private lateinit var fridayteacher: ArrayList<TextView>

    private var deptArray = ArrayList<ArrayList<TextView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)

        // toolbar 설정
        setSupportActionBar(timetable_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        mondaydept = arrayListOf(Mon1_dept, Mon2_dept, Mon3_dept, Mon4_dept, Mon5_dept, Mon6_dept, Mon7_dept)
        tuesdaydept = arrayListOf(Tue1_dept, Tue2_dept, Tue3_dept, Tue4_dept, Tue5_dept, Tue6_dept, Tue7_dept)
        wednesdaydept = arrayListOf(Wed1_dept, Wed2_dept, Wed3_dept, Wed4_dept, Wed5_dept, Wed6_dept, Wed7_dept)
        thursdaydept = arrayListOf(Thu1_dept, Thu2_dept, Thu3_dept, Thu4_dept, Thu5_dept, Thu6_dept, Thu7_dept)
        fridaydept = arrayListOf(Fri1_dept, Fri2_dept, Fri3_dept, Fri4_dept, Fri5_dept, Fri6_dept, Fri7_dept)

        mondayplace = arrayListOf(Mon1_place, Mon2_place, Mon3_place, Mon4_place, Mon5_place, Mon6_place, Mon7_place)
        tuesdayplace = arrayListOf(Tue1_place, Tue2_place, Tue3_place, Tue4_place, Tue5_place, Tue6_place, Tue7_place)
        wednesdayplace = arrayListOf(Wed1_place, Wed2_place, Wed3_place, Wed4_place, Wed5_place, Wed6_place, Wed7_place)
        thursdayplace = arrayListOf(Thu1_place, Thu2_place, Thu3_place, Thu4_place, Thu5_place, Thu6_place, Thu7_place)
        fridayplace = arrayListOf(Fri1_place, Fri2_place, Fri3_place, Fri4_place, Fri5_place, Fri6_place, Fri7_place)

        mondayteacher = arrayListOf(Mon1_teach, Mon2_teach, Mon3_teach, Mon4_teach, Mon5_teach, Mon6_teach, Mon7_teach)
        tuesdayteacher = arrayListOf(Tue1_teach, Tue2_teach, Tue3_teach, Tue4_teach, Tue5_teach, Tue6_teach, Tue7_teach)
        wednesdayteacher = arrayListOf(Wed1_teach, Wed2_teach, Wed3_teach, Wed4_teach, Wed5_teach, Wed6_teach, Wed7_teach)
        thursdayteacher = arrayListOf(Thu1_teach, Thu2_teach, Thu3_teach, Thu4_teach, Thu5_teach, Thu6_teach, Thu7_teach)
        fridayteacher = arrayListOf(Fri1_teach, Fri2_teach, Fri3_teach, Fri4_teach, Fri5_teach, Fri6_teach, Fri7_teach)

        deptArray = arrayListOf(mondaydept, tuesdaydept, wednesdaydept, thursdaydept, fridaydept)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    // 시간표 편집
//    private fun setEditMode() {
//        for (day in dayArray) {
//            for (textView in day)
//                textView.isEnabled = true
//        }
//    }
//
//    // 시간표 편집 완료
//    private fun doneEditMode() {
//        for (day in dayArray) {
//            for (textView in day)
//                textView.isEnabled = false
//        }
//        saveData()
//    }

    // 시간표 저장하는 함수
//    private fun saveData() {
//        val saveArray = ArrayList<HashMap<String, Any>>()
//        val deleteArray = ArrayList<HashMap<String, Any>>()
//
//        for (i in 0..5) {
//            var j = 1
//            for (textView in deptArray[i]) {
//                val map = HashMap<String, Any>()
//                if(textView.text.isNotEmpty()) {
//                    map["subject"] = textView.text.toString()
//                    map["days"] = dayText[i]
//                    map["period"] = j
//                    saveArray.add(map)
//                } else if(textView.text.isEmpty() || textView.text.toString() == "") {
//                    map["subject"] = "delete"
//                    map["days"] = dayText[i]
//                    map["period"] = j
//                    deleteArray.add(map)
//                }
//                j += 1
//            }
//        }
//
//        deleteData(deleteArray)
//
//        val dataMap = HashMap<String, ArrayList<HashMap<String, Any>>>()
//        dataMap["list"] = saveArray
//
//        (application as MasterApplication).service.updateTimeTable(dataMap)
//            .enqueue(object : Callback<HashMap<String, String>> {
//                override fun onResponse(
//                    call: Call<HashMap<String, String>>,
//                    response: Response<HashMap<String, String>>
//                ) {
//                    if (response.isSuccessful) { }
//                    else {        // 3xx, 4xx 를 받은 경우
//                        toast("데이터를 수정할 수 없습니다")
//                        finish()
//                    }
//                }
//
//                // 응답 실패 시
//                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
//                    toast("network error")
//                    finish()
//                }
//            })
//    }

    // 시간표 조회하는 함수
    private fun loadData() {
        (application as MasterApplication).service.readTimeTable()
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>, response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["table"] as LinkedTreeMap<String, LinkedTreeMap<String, LinkedTreeMap<String, String>>>

                        for (i in 0..4) {
                            val todayList = data[dayText[i]] ?: continue

                            lateinit var deptList: ArrayList<TextView>
                            lateinit var placeList: ArrayList<TextView>
                            lateinit var teacherList: ArrayList<TextView>

                            when (dayText[i]) {
                                "mon" -> {
                                    deptList = mondaydept
                                    placeList = mondayplace
                                    teacherList = mondayteacher
                                }
                                "tue" -> {
                                    deptList = tuesdaydept
                                    placeList = tuesdayplace
                                    teacherList = tuesdayteacher
                                }
                                "wed" -> {
                                    deptList = wednesdaydept
                                    placeList = wednesdayplace
                                    teacherList = wednesdayteacher
                                }
                                "thu" -> {
                                    deptList = thursdaydept
                                    placeList = thursdayplace
                                    teacherList = thursdayteacher
                                }
                                "fri" -> {
                                    deptList = fridaydept
                                    placeList = fridayplace
                                    teacherList = fridayteacher
                                }
                            }

                            for (j in 0..6) {
                                val map = todayList["t${j + 1}"] ?: continue

                                deptList[j].text = map["subject"]
                                if (map["location"] == " ") {
                                    placeList[j].visibility = View.GONE
                                }
                                else {
                                    placeList[j].text = map["location"]
                                    placeList[j].visibility = View. VISIBLE
                                }
                                if (map["teacher"] == " ") teacherList[j].visibility = View.GONE
                                else {
                                    teacherList[j].text = map["teacher"]
                                    teacherList[j].visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("시간표를 조회할 수 없습니다")
                        finish()
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
        menuInflater.inflate(R.menu.timetable_menu, menu)
        return true
    }

    fun addDeptOnClick(item: MenuItem) {
        var day: String? = null

        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.timetableadd_dialog, null)
        val deptEditText: EditText = view.findViewById(R.id.timetable_dialog_dept)
        val locationEditText = view.findViewById<EditText>(R.id.timetable_dialog_location)
        val teacherEditText = view.findViewById<EditText>(R.id.timetable_dialog_teacher)

        // 다이얼로그 스피너 설정
        val spinner = view.findViewById<Spinner>(R.id.timetable_dialog_period_spinner)
        val periodList = arrayOf("1", "2", "3", "4", "5", "6", "7")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, periodList)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { }
        }

        // 다이얼로그 라디오그룹 설정
        val radioGroup = view.findViewById<RadioGroup>(R.id.timetable_dialog_radiogroup)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radioMon -> day = "mon"
                R.id.radioTue -> day = "tue"
                R.id.radioWed -> day = "wed"
                R.id.radioThu -> day = "thu"
                R.id.radioFri -> day = "fri"
            }
        }
        builder.setView(view)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()

        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                if (deptEditText.text.toString() == "") {
                    toast("수업 이름을 입력해 주세요")
                } else if (day == null) {
                    toast("요일을 선택해 주세요")
                } else {
                    val dept = deptEditText.text.toString()
                    val period = spinner.selectedItem.toString().toInt()
                    val location: String? = locationEditText.text.toString()
                    val teacher: String? = teacherEditText.text.toString()

                    createTable(dept, day!!, period, location, teacher)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
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

    private fun deleteData(array: ArrayList<HashMap<String, Any>>) {
        val deleteMap = HashMap<String, ArrayList<HashMap<String, Any>>>()

        deleteMap["list"] = array

        (application as MasterApplication).service.deleteTimeTable(deleteMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) { }
                    else {        // 3xx, 4xx 를 받은 경우
                        toast("시간표를 삭제할 수 없습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    private fun createTable(dept: String, day: String, period: Int, location: String?, teacher: String?) {
        val map = HashMap<String, Any?>()

        map["subject"] = dept
        map["days"] = day
        map["period"] = period
        map["location"] = if (location != null && location != "") location
        else " "
        map["teacher"] = if (teacher != null && teacher != "") teacher
        else " "

        val arrayList = ArrayList<HashMap<String, Any?>>()
        arrayList.add(map)
        val sendMap = HashMap<String, ArrayList<HashMap<String, Any?>>>()
        sendMap["list"] = arrayList

        (application as MasterApplication).service.updateTimeTable(sendMap)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") {
                        toast("시간표 등록이 완료되었습니다")
                        loadData()
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("시간표 등록에 실패했습니다")
                        finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })

    }
}
