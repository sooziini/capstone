package com.example.capstone.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.example.capstone.R
import com.example.capstone.dataclass.Meal

class MealFragmentAdapter(
    private val itemList: ArrayList<Meal>,
    private val todayDate: String,
    private val inflater: LayoutInflater,
    private val context: Context
): RecyclerView.Adapter<MealFragmentAdapter.MealFragmentViewHolder>() {

    inner class MealFragmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val detailRv = itemView.findViewById<RecyclerView>(R.id.meal_fragment_rv2)
        val date = itemView.findViewById<TextView>(R.id.meal_fragment_datetext)
        val layout = itemView.findViewById<LinearLayout>(R.id.meal_fragment_layout)

        fun bind(meal: Meal) {
            date.text = "${meal.year}년 ${meal.month}월 ${meal.day}일"
            detailRv.adapter = MealDetailAdapter(meal.mealFragmentItemList, inflater)
            detailRv.layoutManager = LinearLayoutManager(context)
            detailRv.setHasFixedSize(true)

            if (meal.date == todayDate)
                layout.background = context.resources.getDrawable(R.drawable.round_border_main_color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealFragmentViewHolder {
        val view = inflater.inflate(R.layout.school_meal_item, parent, false)
        return MealFragmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MealFragmentViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
}