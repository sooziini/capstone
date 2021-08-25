package com.example.capstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Meal

class MealActAdapter(
    private val mealList: ArrayList<Meal>,
    private val inflater: LayoutInflater
): RecyclerView.Adapter<MealActAdapter.MealActViewHolder>() {

    inner class MealActViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val comp: TextView? = itemView.findViewById(R.id.SchoolMeal_ItemText)

        fun bind(meal: Meal) {
            if(mealList.size == 0)
                return
            comp?.text = meal.comp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealActViewHolder {
        val view: View = if (mealList.size != 0)
            inflater.inflate(R.layout.school_meal_item, parent, false)
        else
            inflater.inflate(R.layout.school_meal_null_item, parent, false)
        return MealActViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mealList.size == 0)
            return 1
        return mealList.size
    }

    override fun onBindViewHolder(holder: MealActViewHolder, position: Int) {
        if (mealList.size != 0)
            holder.bind(mealList[position])
    }
}