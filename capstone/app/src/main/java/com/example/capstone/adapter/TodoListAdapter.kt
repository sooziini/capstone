package com.example.capstone.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
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
    private val context: Context,
    private val date: String
): RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    private val dbHelper = TodoListDBHelper(context)

    inner class TodoListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val box: CheckBox? = itemView.findViewById(R.id.TodoList_Item_CheckBox)
        val db = dbHelper.writableDatabase!!

        fun bind(todo: Todo) {

            if(todoList.size == 0)
                return

            box?.text = todo.body
            if (todo.checked == "true")
                box?.isChecked = true
            else if (todo.checked == "false")
                box?.isChecked = false

            box?.setOnCheckedChangeListener { _, isChecked ->
                val dbHelper = TodoListDBHelper(context)
                val db = dbHelper.writableDatabase

                val contentVal = ContentValues()
                contentVal.put(TodoEntry.COLUMN_NAME_CHECK, isChecked.toString())

                val arg = arrayOf(box.text.toString())
                db.update(TodoEntry.TABLE_NAME, contentVal, "${TodoEntry.COLUMN_NAME_TODOLIST} = ?", arg)
            }

            box?.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("일정 삭제")
                builder.setMessage("해당 일정을 삭제하시겠습니까?")
                builder.setIcon(R.drawable.ic_todo_delete)

                val listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val args = arrayOf(date, todo.body)
                                db.delete(
                                    TodoEntry.TABLE_NAME,
                                    "${TodoEntry.COLUMN_NAME_DATE} = ? AND ${TodoEntry.COLUMN_NAME_TODOLIST} = ?",
                                    args
                                )

                                box.visibility = View.GONE
                            }
                            DialogInterface.BUTTON_NEGATIVE ->
                                return
                        }
                    }
                }

                builder.setPositiveButton("삭제", listener)
                builder.setNegativeButton("취소", listener)

                builder.show()

                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val view: View = if (todoList.size != 0)
            inflater.inflate(R.layout.todolist_item, parent, false)
        else
            inflater.inflate(R.layout.todolist_null_item, parent, false)
        return TodoListViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (todoList.size == 0)
            return 1

        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        if (todoList.size != 0)
            holder.bind(todoList[position])
    }
}