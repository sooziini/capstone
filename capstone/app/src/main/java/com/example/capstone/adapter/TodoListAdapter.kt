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
import com.example.capstone.main.TodoListActivity
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
    private val application: Application,
    private val ver: Int
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class TodoListFragmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val box: CheckBox? = itemView.findViewById(R.id.TodoList_Item_CheckBox)

        fun bind(todo: Todo) {
            box?.text = todo.body
            if (todo.checked == "true")
                box?.isChecked = true
            else if (todo.checked == "false")
                box?.isChecked = false

            box?.setOnCheckedChangeListener { _, isChecked ->
                retrofitCheckTodo(todo.list_id)
            }
        }
    }

    inner class TodoListActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val box: CheckBox? = itemView.findViewById(R.id.TodoList_Item_CheckBox)

        fun bind(todo: Todo, position: Int) {
            box?.text = todo.body
            if (todo.checked == "true")
                box?.isChecked = true
            else if (todo.checked == "false")
                box?.isChecked = false

            box?.setOnCheckedChangeListener { _, isChecked ->
                retrofitCheckTodo(todo.list_id)
            }

            box?.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
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
                                                context.toast("할 일 수정 실패")
                                            }
                                        }

                                        // 응답 실패 시
                                        override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
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
                                                removeTodoItem(position)
                                            } else {        // 3xx, 4xx 를 받은 경우
                                                context.toast("할 일 삭제 실패")
                                            }
                                        }

                                        // 응답 실패 시
                                        override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
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

    fun retrofitCheckTodo(list_id: Int) {
        (application as MasterApplication).service.checkTodo(list_id)
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

    fun removeTodoItem(position: Int) {
        todoList.removeAt(position)
        notifyDataSetChanged()
        if (todoList.isEmpty() && ver == 0) {
            (context as TodoListActivity).setChange(true)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ver
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.todolist_item, parent, false)
        return when(viewType) {
            0 -> TodoListActivityViewHolder(view)
            else -> TodoListFragmentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(ver) {
            0 -> (holder as TodoListActivityViewHolder).bind(todoList[position], position)
            else -> (holder as TodoListFragmentViewHolder).bind(todoList[position])
        }
    }
}