package com.example.capstone.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.MasterStudent
import com.example.capstone.network.MasterApplication
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StuListAdapter(
    private val stuList: ArrayList<MasterStudent>,
    private val inflater: LayoutInflater,
    private val context: Context,
    private val application: Application
): RecyclerView.Adapter<StuListAdapter.StuListViewHolder>() {

    inner class StuListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val stuid = itemView.findViewById<TextView>(R.id.stulist_item_stuid)
        private val stuname = itemView.findViewById<TextView>(R.id.stulist_item_name)
        private val role = itemView.findViewById<Spinner>(R.id.stulist_role_dropdown)

        fun bind(student: MasterStudent) {
            stuid.text = student.studentId
            stuname.text = student.name

            // role 드롭다운
            val roleList = arrayOf("학생회", "학생")
            role.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, roleList)
            if (student.role == "학생") role.setSelection(1)
            role.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) { }
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (student.role != role.selectedItem.toString()) {
                        student.role = role.selectedItem.toString()
                        changeRole(student.id, student.role)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuListViewHolder {
        val view = inflater.inflate(R.layout.stulist_item, parent, false)
        return StuListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stuList.size
    }

    override fun onBindViewHolder(holder: StuListViewHolder, position: Int) {
        holder.bind(stuList[position])
    }

    // role 변경
    private fun changeRole(id: String, role: String) {
        val map = HashMap<String, String>()
        map["user_id"] = id
        map["role"] = if (role == "학생회") "leader"
        else "student"

        (application as MasterApplication).service.changeRole(map)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!["success"].toString() == "true") { }
                    else context.toast("변경 실패")
                }
                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    context.toast("network error")
                }
            })
    }
}