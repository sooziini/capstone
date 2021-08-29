package com.example.capstone.adapter

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Todo
import com.example.capstone.network.MasterApplication
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoListAdapter(
    private val todoList: ArrayList<Todo>,
    private val inflater: LayoutInflater,
    private val context: Context,
    private val application: Application
): RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    inner class TodoListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val box: CheckBox? = itemView.findViewById(R.id.TodoList_Item_CheckBox)

        fun bind(todo: Todo) {

            if(todoList.size == 0)
                return

            box?.text = todo.body
            if (todo.checked == "true")
                box?.isChecked = true
            else if (todo.checked == "false")
                box?.isChecked = false

            box?.setOnCheckedChangeListener { _, isChecked ->
                (application as MasterApplication).service.checkTodo(todo.list_id)
                    .enqueue(object : Callback<HashMap<String, String>> {
                        override fun onResponse(
                            call: Call<HashMap<String, String>>,
                            response: Response<HashMap<String, String>>
                        ) {
                            if (response.isSuccessful) {

                            } else {        // 3xx, 4xx 를 받은 경우
                                context.toast("체크 실패")
                            }
                        }

                        // 응답 실패 시
                        override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                            context.toast("network error")
                        }
                    })
            }

            box?.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("일정 수정")
                builder.setIcon(R.drawable.ic_todo_menu_edit)

                val view = inflater.inflate(R.layout.todoadd_dialog, null)
                builder.setView(view)

                val listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE -> {// 일정 수정 버튼
                                val alert = p0 as AlertDialog
                                val edit = alert.findViewById<EditText>(R.id.TodoBody)
                                val map = HashMap<String, String>()

                                map["body"] = edit.text.toString()

                                (application as MasterApplication).service.updateTodo(todo.list_id, map)
                                    .enqueue(object : Callback<HashMap<String, String>> {
                                        override fun onResponse(
                                            call: Call<HashMap<String, String>>,
                                            response: Response<HashMap<String, String>>
                                        ) {
                                            if (response.isSuccessful) {
                                                box.text = edit.text
                                            } else {        // 3xx, 4xx 를 받은 경우
                                                context.toast("체크 실패")
                                            }
                                        }

                                        // 응답 실패 시
                                        override fun onFailure(
                                            call: Call<HashMap<String, String>>,
                                            t: Throwable
                                        ) {
                                            context.toast("network error")
                                        }
                                    })
                            }
                            DialogInterface.BUTTON_NEGATIVE -> // 취소 버튼
                                return
                            DialogInterface.BUTTON_NEUTRAL -> {// 일정 삭제 버튼
                                (application as MasterApplication).service.deleteTodo(todo.list_id)
                                    .enqueue(object : Callback<HashMap<String, String>> {
                                        override fun onResponse(
                                            call: Call<HashMap<String, String>>,
                                            response: Response<HashMap<String, String>>
                                        ) {
                                            if (response.isSuccessful) {
                                                context.toast("일정이 삭제되었습니다.")
                                                box.visibility = View.GONE
                                            } else {        // 3xx, 4xx 를 받은 경우
                                                context.toast("일정 삭제 실패")
                                            }
                                        }

                                        // 응답 실패 시
                                        override fun onFailure(
                                            call: Call<HashMap<String, String>>,
                                            t: Throwable
                                        ) {
                                            context.toast("network error")
                                        }
                                    })
                            }
                        }
                    }
                }

                builder.setPositiveButton("수정", listener)
                builder.setNegativeButton("취소", listener)
                builder.setNeutralButton("삭제", listener)

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