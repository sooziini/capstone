package com.example.capstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.dataclass.Meal
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.padding

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
            if (itemList.size == 0)
                return

            date.text = "${meal.year}년 ${meal.month}월 ${meal.day}일"
            detailRv.adapter = MealDetailAdapter(meal.mealFragmentItemList, inflater)
            detailRv.layoutManager = LinearLayoutManager(context)
            detailRv.setHasFixedSize(true)

            if (meal.date == todayDate) {
                layout.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_border_main_color)
                layout.padding = 10
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealFragmentViewHolder {
        val view = if (itemList.size != 0)
            inflater.inflate(R.layout.school_meal_item, parent, false)
        else
            inflater.inflate(R.layout.school_meal_null_item, parent, false)
        return MealFragmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (itemList.size == 0)
            1
        else
            itemList.size
    }

    override fun onBindViewHolder(holder: MealFragmentViewHolder, position: Int) {
        if (itemList.size != 0)
            holder.bind(itemList[position])
    }
}