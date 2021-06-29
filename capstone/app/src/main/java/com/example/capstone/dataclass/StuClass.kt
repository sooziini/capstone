package com.example.capstone.dataclass

import java.io.Serializable

data class StuClass (
    val classNum : Int,
    val className : String,
    val startTime : String,
    val endTime : String
): Serializable