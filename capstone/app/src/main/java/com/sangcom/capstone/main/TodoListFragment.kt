package com.sangcom.capstone.main

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sangcom.capstone.R
import com.sangcom.capstone.adapter.TodoListAdapter
import com.sangcom.capstone.dataclass.Todo
import com.sangcom.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_todo_list.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class TodoListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getList()
    }

    private fun getList() {
        val list = ArrayList<Todo>()
        val instance = Calendar.getInstance()
        val year = instance.get(Calendar.YEAR)
        val month = (instance.get(Calendar.MONTH) + 1)
        val day = instance.get(Calendar.DAY_OF_MONTH)

        (activity?.application as MasterApplication).service.readTodo(year, month, day)
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

                            list.add(Todo(list_id, body, check))
                        }

                        if (list.isEmpty()) {
                            setChange(true)
                        } else {
                            setChange(false)
                            TodoFrg_Recycleriew.adapter = TodoListAdapter(list, LayoutInflater.from(activity), requireContext(), activity?.application!!, 1)
                            TodoFrg_Recycleriew.layoutManager = LinearLayoutManager(activity)
                            TodoFrg_Recycleriew.setHasFixedSize(true)
                        }
                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("할 일을 조회할 수 없습니다")
                        (context as Activity).finish()
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("network error")
                    (context as Activity).finish()
                }
            })
    }

    fun setChange(ch: Boolean) {
        if (ch) {   // 할 일 없음
            TodoFrg_Recycleriew.visibility = View.GONE
            todoList_null_item_frag.visibility = View.VISIBLE
        } else {    // 할 일 있음
            TodoFrg_Recycleriew.visibility = View.VISIBLE
            todoList_null_item_frag.visibility = View.GONE
        }
    }
}