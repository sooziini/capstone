package com.example.capstone.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.TodoListAdapter
import com.example.capstone.dataclass.Todo
import com.example.capstone.network.MasterApplication
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_todo_list.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
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
                        TodoFrg_Recycleriew.adapter = TodoListAdapter(list, LayoutInflater.from(activity), requireContext(), activity?.application!!)
                        TodoFrg_Recycleriew.layoutManager = LinearLayoutManager(activity)
                        TodoFrg_Recycleriew.setHasFixedSize(true)
                        Log.d("list1", list.toString())

                    } else {        // 3xx, 4xx 를 받은 경우
                        toast("TodoList 로드 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                    toast("todolist network error")
                }
            })
    }
}