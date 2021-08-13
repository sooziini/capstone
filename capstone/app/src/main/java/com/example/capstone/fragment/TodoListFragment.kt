package com.example.capstone.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.TodoListAdapter
import com.example.capstone.database.TodoEntry
import com.example.capstone.database.TodoListDBHelper
import com.example.capstone.dataclass.Todo
import kotlinx.android.synthetic.main.fragment_todo_list.*
import java.util.*
import kotlin.collections.ArrayList

class TodoListFragment : Fragment() {
    lateinit var dbHelper: TodoListDBHelper
    lateinit var todoList: ArrayList<Todo>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dbHelper = TodoListDBHelper(requireContext())
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoList = getList()

        TodoFrg_Recycleriew.adapter = TodoListAdapter(todoList, LayoutInflater.from(this.activity), requireContext())
        TodoFrg_Recycleriew.layoutManager = LinearLayoutManager(this.activity)
        TodoFrg_Recycleriew.setHasFixedSize(true)
    }

    private fun getList(): ArrayList<Todo> {
        val todoList = ArrayList<Todo>()

        val instance = Calendar.getInstance()
        var year = instance.get(Calendar.YEAR).toString()
        var month = (instance.get(Calendar.MONTH) + 1).toString()
        var day = instance.get(Calendar.DAY_OF_MONTH).toString()
        if (month.length < 2)
            month = "0${month}"
        if (day.length < 2)
            day = "0${day}"
        val date = year + month + day

        val db = dbHelper.readableDatabase
        val projection = arrayOf(TodoEntry.COLUMN_NAME_TODOLIST, TodoEntry.COLUMN_NAME_CHECK)
        val selection = "${TodoEntry.COLUMN_NAME_DATE} = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query(TodoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
        with(cursor) {
            while(moveToNext()) {
                val todo = Todo(
                    body = cursor.getString(getColumnIndexOrThrow(TodoEntry.COLUMN_NAME_TODOLIST)),
                    checked = cursor.getString(getColumnIndexOrThrow(TodoEntry.COLUMN_NAME_CHECK))
                )
                todoList.add(todo)
            }
        }
        return todoList
    }

}