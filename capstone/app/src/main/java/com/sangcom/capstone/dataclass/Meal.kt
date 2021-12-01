package com.sangcom.capstone.dataclass

data class Meal (
    val year: String,
    val month: String,
    val day: String,
    val mealFragmentItemList: ArrayList<String>,
    val date: String = year + month + day
)
