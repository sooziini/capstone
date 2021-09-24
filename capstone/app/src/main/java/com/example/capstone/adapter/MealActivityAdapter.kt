package com.example.capstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Meal

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
            week.text = "${weekNum}주차"
            activityRv.adapter = MealFragmentAdapter(mealList, "00000000", inflater, context)
            activityRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            activityRv.setHasFixedSize(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealActivityViewHolder {
        val view = inflater.inflate(R.layout.school_meal_activity_item, parent, false)
        return MealActivityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MealActivityViewHolder, position: Int) {
        holder.bind(itemList[position], weekList[position])
    }
}