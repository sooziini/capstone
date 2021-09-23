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
            comp?.text = meal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDetailViewHolder {
        val view: View = inflater.inflate(R.layout.school_meal_itemdetail, parent, false)
        return MealDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    override fun onBindViewHolder(holder: MealDetailViewHolder, position: Int) {
        holder.bind(mealList[position])
    }
}