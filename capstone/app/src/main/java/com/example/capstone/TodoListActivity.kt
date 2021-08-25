package com.example.capstone

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
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
import com.example.capstone.database.TodoEntry
import com.example.capstone.database.TodoListDBHelper
import com.example.capstone.dataclass.Todo
import kotlinx.android.synthetic.main.activity_todo_list.*
import java.util.*
import kotlin.collections.ArrayList

class TodoListActivity : AppCompatActivity() {
    private var imm: InputMethodManager? = null
    private lateinit var dbHelper: TodoListDBHelper
    private val calendar = Calendar.getInstance()
    private val todoList = ArrayList<Todo>()
    lateinit var date: String
    lateinit var adapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        dbHelper = TodoListDBHelper(this)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val tempMon = setDateSize((month + 1).toString())
        val tempDay = setDateSize(day.toString())
        date = "$year" + tempMon + tempDay

        TodoList_DateTextView.text = "${year}년 ${tempMon}월 ${tempDay}일"

        // toolbar 설정
        setSupportActionBar(todolist_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        loadTodo(date)

        // Calendar 버튼 설정 (Dialog)
        TodoList_CalButton.setOnClickListener {
            val listener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                // i년 i2월 i3일
                val tempi2 = setDateSize((i2 + 1).toString())
                val tempi3 = setDateSize(i3.toString())
                TodoList_DateTextView.text = "${i}년 ${tempi2}월 ${tempi3}일"
                date = "$year" + tempi2 + tempi3
                loadTodo(date)
            }

            val picker = DatePickerDialog(this, listener, year, month, day)
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

    private fun saveTodo(date: String, body: String) {
        val db = dbHelper.writableDatabase

        val contentVal = ContentValues()
        contentVal.put(TodoEntry.COLUMN_NAME_DATE, date)
        contentVal.put(TodoEntry.COLUMN_NAME_CHECK, false.toString())
        contentVal.put(TodoEntry.COLUMN_NAME_TODOLIST, body)

        db.insert(TodoEntry.TABLE_NAME, null, contentVal)
    }

    private fun loadTodo(date: String) {
        val db = dbHelper.readableDatabase

        todoList.clear()

        val projection = arrayOf(TodoEntry.COLUMN_NAME_TODOLIST, TodoEntry.COLUMN_NAME_CHECK)
        val selection = "${TodoEntry.COLUMN_NAME_DATE} = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query(TodoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
        with(cursor) {
            while(moveToNext()) {
                val todo = Todo(cursor.getString(getColumnIndexOrThrow(TodoEntry.COLUMN_NAME_TODOLIST)), cursor.getString(getColumnIndexOrThrow(TodoEntry.COLUMN_NAME_CHECK)))
                todoList.add(todo)
            }
        }
        adapter = TodoListAdapter(todoList, LayoutInflater.from(this@TodoListActivity), this, date)
        TodoAct_RecyclerView.adapter = adapter
        TodoAct_RecyclerView.layoutManager = LinearLayoutManager(this@TodoListActivity)
        TodoAct_RecyclerView.setHasFixedSize(true)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todolist_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 할일 추가 버튼
    fun todoAddOnClick(item: MenuItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("할 일 추가")
        builder.setIcon(R.drawable.ic_baseline_star_24)

        val view = layoutInflater.inflate(R.layout.todoadd_dialog, null)
        builder.setView(view)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        val listener = DialogInterface.OnClickListener { p0, _ ->
            val alert = p0 as AlertDialog
            val edit: EditText? = alert.findViewById(R.id.TodoBody)

//            tv1.text = "${edit1?.text}"
//            tv1.append("${edit2?.text}")
            saveTodo(date, edit?.text.toString())
            loadTodo(date)
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