package com.example.capstone

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.TodoListAdapter
import com.example.capstone.dataclass.Todo
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_todo_list.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class TodoListActivity : AppCompatActivity() {
    private var imm: InputMethodManager? = null
    private val calendar = Calendar.getInstance()
    private val todoList = ArrayList<Todo>()
    lateinit var adapter: TodoListAdapter

    private val todayYear = calendar.get(Calendar.YEAR)
    private val todayMonth = calendar.get(Calendar.MONTH)
    private val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

    private var year = todayYear
    private var month = todayMonth
    private var day = todayDay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        val tempMon = setDateSize((month + 1).toString())
        val tempDay = setDateSize(day.toString())

        TodoList_DateTextView.text = "${year}년 ${tempMon}월 ${tempDay}일"

        // toolbar 설정
        setSupportActionBar(todolist_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        loadTodo(todayYear, todayMonth, todayDay)

        // Calendar 버튼 설정 (Dialog)
        TodoList_CalButton.setOnClickListener {
            val listener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                // i년 i2월 i3일
                val tempi2 = setDateSize((i2 + 1).toString())
                val tempi3 = setDateSize(i3.toString())
                TodoList_DateTextView.text = "${i}년 ${tempi2}월 ${tempi3}일"
                year = i
                month = i2
                day = i3

                loadTodo(year, month, day)
            }

            val picker = DatePickerDialog(this, listener, todayYear, todayMonth, todayDay)
            picker.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun saveTodo(year: Int, month: Int, day: Int, body: String) {
        val map = HashMap<String, Any>()

        map["body"] = body
        map["year"] = year
        map["month"] = month
        map["day"] = day

        (application as MasterApplication).service.createTodo(map)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful) {
                        loadTodo(year, month - 1, day)
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("TodoList 등록 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })

    }

    private fun loadTodo(year: Int, month: Int, day: Int) {
        todoList.clear()

        (application as MasterApplication).service.readTodo(year, month + 1, day)
            .enqueue(object : Callback<HashMap<String, Any>> {
                override fun onResponse(
                    call: Call<HashMap<String, Any>>,
                    response: Response<HashMap<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!["todoList"] as ArrayList<LinkedTreeMap<String, Any>>

                        for (map in data) {
                            val list_id = (map["list_id"] as Double).roundToInt()
                            val body = map["body"] as String
                            val check = if ((map["listCheck"] as Double).roundToInt() == 0)
                                "false"
                            else
                                "true"

                            todoList.add(Todo(list_id, body, check))
                        }
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("TodoList 로드 실패")
                    }
                    if (todoList.isEmpty()) {
                        setChange(true)
                    } else {
                        setChange(false)
                        TodoAct_RecyclerView.adapter =  TodoListAdapter(todoList, LayoutInflater.from(this@TodoListActivity), this@TodoListActivity, application, 0)
                        TodoAct_RecyclerView.layoutManager = LinearLayoutManager(this@TodoListActivity)
                        TodoAct_RecyclerView.setHasFixedSize(true)
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    finish()
                }
            })
    }

    fun setChange(ch: Boolean) {
        if (ch) {   // 할 일 없음
            TodoAct_RecyclerView.visibility = View.GONE
            todoList_null_item.visibility = View.VISIBLE
        } else {    // 할 일 있음
            TodoAct_RecyclerView.visibility = View.VISIBLE
            todoList_null_item.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todolist_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 할 일 추가 버튼
    fun todoAddOnClick(item: MenuItem) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.todoadd_dialog, null)
        builder.setView(view)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        val listener = DialogInterface.OnClickListener { p0, _ ->
            val alert = p0 as AlertDialog
            val edit: EditText? = alert.findViewById(R.id.TodoBody)

            saveTodo(year, month + 1, day, edit?.text.toString())
        }

        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    private fun setDateSize(data: String): String {
        return if (data.length < 2) {
            val temp = "0${data}"
            temp
        } else
            data
    }
}