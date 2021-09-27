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
import org.jetbrains.anko.textColor
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MealFragmentAdapter(
    private val itemList: ArrayList<Meal>,
    private val todayDate: String,
    private val inflater: LayoutInflater,
    private val context: Context
): RecyclerView.Adapter<MealFragmentAdapter.MealFragmentViewHolder>() {

    val cal = Calendar.getInstance()
    val df: DateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))

    inner class MealFragmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val detailRv = itemView.findViewById<RecyclerView>(R.id.meal_fragment_rv2)
        val date = itemView.findViewById<TextView>(R.id.meal_fragment_datetext)
        val layout = itemView.findViewById<LinearLayout>(R.id.meal_fragment_layout)

        fun bind(meal: Meal) {
            if (itemList.size == 0)
                return

            val now: Date? = df.parse(meal.date)
            cal.time = now!!
            val dayn = when (cal.get(Calendar.DAY_OF_WEEK)) {
                1 -> "일"
                2 -> "월"
                3 -> "화"
                4 -> "수"
                5 -> "목"
                6 -> "금"
                7 -> "토"
                else -> "-"
            }

            date.text = "${meal.month.toInt()}월 ${meal.day.toInt()}일 (${dayn})"
            detailRv.adapter = MealDetailAdapter(meal.mealFragmentItemList, inflater)
            detailRv.layoutManager = LinearLayoutManager(context)
            detailRv.setHasFixedSize(true)

            if (meal.date == todayDate)
                date.textColor = ContextCompat.getColor(context, R.color.main_color)
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