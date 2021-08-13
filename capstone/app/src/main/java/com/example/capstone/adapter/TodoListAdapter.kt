package com.example.capstone.adapter

import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.database.TodoEntry
import com.example.capstone.database.TodoListDBHelper
import com.example.capstone.dataclass.Todo

class TodoListAdapter(
    private val todoList: ArrayList<Todo>,
    private val inflater: LayoutInflater,
    private val context: Context
): RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {

    inner class TodoListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val box: CheckBox = itemView.findViewById(R.id.TodoList_Item_CheckBox)

        fun bind(todo: Todo) {
            box.text = todo.body
            if (todo.checked == "true")
                box.isChecked = true
            else if (todo.checked == "false")
                box.isChecked = false

            box.setOnCheckedChangeListener { compoundButton, isChecked ->
                val dbHelper = TodoListDBHelper(context)
                val db = dbHelper.writableDatabase

                val contentVal = ContentValues()
                contentVal.put(TodoEntry.COLUMN_NAME_CHECK, isChecked.toString())

                val arg = arrayOf(box.text.toString())
                db.update(TodoEntry.TABLE_NAME, contentVal, "${TodoEntry.COLUMN_NAME_TODOLIST} = ?", arg)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val view = inflater.inflate(R.layout.todolist_item, parent, false)
        return TodoListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.bind(todoList[position])
    }
}