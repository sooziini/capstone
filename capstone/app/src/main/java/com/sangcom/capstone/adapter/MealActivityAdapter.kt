package com.sangcom.capstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sangcom.capstone.R
import com.sangcom.capstone.dataclass.Meal

class MealActivityAdapter(
    private val itemList: ArrayList<ArrayList<Meal>>,
    private val weekList: ArrayList<Int>,
    private val inflater: LayoutInflater,
    private val context: Context
): RecyclerView.Adapter<MealActivityAdapter.MealActivityViewHolder>() {

    inner class MealActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val activityRv = itemView.findViewById<RecyclerView>(R.id.meal_activity_rv2)
        val week = itemView.findViewById<TextView>(R.id.meal_activity_weektext)

        fun bind(mealList: ArrayList<Meal>, weekNum: Int) {
            if (itemList.size == 0)
                return

            week.text = "${weekNum}주차"
            activityRv.adapter = MealFragmentAdapter(mealList, "00000000", inflater, context)
            activityRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            activityRv.setHasFixedSize(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealActivityViewHolder {
        val view = if (itemList.size != 0)
            inflater.inflate(R.layout.school_meal_activity_item, parent, false)
        else inflater.inflate(R.layout.school_meal_null_item, parent, false)
        return MealActivityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(itemList.size == 0)
            1
        else
            itemList.size
    }

    override fun onBindViewHolder(holder: MealActivityViewHolder, position: Int) {
        if (itemList.size != 0)
            holder.bind(itemList[position], weekList[position])
    }
}