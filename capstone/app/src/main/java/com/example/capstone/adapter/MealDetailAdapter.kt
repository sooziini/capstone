package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R

class MealDetailAdapter(
    private val mealList: ArrayList<String>,
    private val inflater: LayoutInflater
): RecyclerView.Adapter<MealDetailAdapter.MealDetailViewHolder>() {

    inner class MealDetailViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val comp: TextView? = itemView.findViewById(R.id.schoolmeal_itemtext)

        fun bind(meal: String) {
            if(mealList.size == 0)
                return
            comp?.text = meal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDetailViewHolder {
        val view: View = if (mealList.size != 0)
            inflater.inflate(R.layout.school_meal_itemdetail, parent, false)
        else
            inflater.inflate(R.layout.school_meal_null_item, parent, false)
        return MealDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mealList.size == 0)
            return 1
        return mealList.size
    }

    override fun onBindViewHolder(holder: MealDetailViewHolder, position: Int) {
        if (mealList.size != 0)
            holder.bind(mealList[position])
    }
}